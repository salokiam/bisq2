/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.network.p2p.services.data.storage.auth;

import bisq.common.encoding.Hex;
import bisq.common.util.MathUtils;
import bisq.common.validation.NetworkDataValidation;
import bisq.network.p2p.services.data.AddDataRequest;
import bisq.network.p2p.services.data.storage.DistributedData;
import bisq.security.DigestUtil;
import bisq.security.KeyGeneration;
import bisq.security.SignatureUtil;
import com.google.protobuf.ByteString;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Optional;

/**
 * Request for adding authenticated data to the data storage.
 * A signature of the authenticatedSequentialData is verified with the given public key.
 * The data gets compared with existing map entries and need to be deterministic.
 */
@EqualsAndHashCode
@Slf4j
public final class AddAuthenticatedDataRequest implements AuthenticatedDataRequest, AddDataRequest {
    public static AddAuthenticatedDataRequest from(AuthenticatedDataStorageService store, AuthenticatedData authenticatedData, KeyPair keyPair)
            throws GeneralSecurityException {

        byte[] hash = DigestUtil.hash(authenticatedData.serialize());
        byte[] pubKeyHash = DigestUtil.hash(keyPair.getPublic().getEncoded());
        int sequenceNumber = store.getSequenceNumber(hash) + 1;
        AuthenticatedSequentialData data = new AuthenticatedSequentialData(authenticatedData, sequenceNumber, pubKeyHash, System.currentTimeMillis());
        byte[] serialized = data.serialize();
        byte[] signature = SignatureUtil.sign(serialized, keyPair.getPrivate());
         /*  log.error("hash={}", Hex.encode(hash));
        log.error("keyPair.getPublic().getEncoded()={}", Hex.encode(keyPair.getPublic().getEncoded()));
        log.error("pubKeyHash={}", Hex.encode(pubKeyHash));
        log.error("sequenceNumber={}", sequenceNumber);
        log.error("serialized={}", Hex.encode(serialized));
        log.error("signature={}", Hex.encode(signature));
        log.error("data={}", data);*/
        return new AddAuthenticatedDataRequest(data, signature, keyPair.getPublic());
    }

    @Getter
    private final AuthenticatedSequentialData authenticatedSequentialData;
    @Getter
    private final byte[] signature;
    @Getter
    private final byte[] ownerPublicKeyBytes;
    private transient final PublicKey ownerPublicKey;

    public AddAuthenticatedDataRequest(AuthenticatedSequentialData authenticatedSequentialData, byte[] signature, PublicKey ownerPublicKey) {
        this(authenticatedSequentialData,
                signature,
                ownerPublicKey.getEncoded(),
                ownerPublicKey);
    }

    private AddAuthenticatedDataRequest(AuthenticatedSequentialData authenticatedSequentialData,
                                        byte[] signature,
                                        byte[] ownerPublicKeyBytes,
                                        PublicKey ownerPublicKey) {
        this.authenticatedSequentialData = authenticatedSequentialData;
        this.signature = signature;
        this.ownerPublicKeyBytes = ownerPublicKeyBytes;
        this.ownerPublicKey = ownerPublicKey;

        NetworkDataValidation.validateECPubKey(ownerPublicKeyBytes);
        NetworkDataValidation.validateECSignature(signature);
    }

    @Override
    public bisq.network.protobuf.EnvelopePayloadMessage toProto() {
        return getNetworkMessageBuilder().setDataRequest(getDataRequestBuilder().setAddAuthenticatedDataRequest(
                bisq.network.protobuf.AddAuthenticatedDataRequest.newBuilder()
                        .setAuthenticatedSequentialData(authenticatedSequentialData.toProto())
                        .setSignature(ByteString.copyFrom(signature))
                        .setOwnerPublicKeyBytes(ByteString.copyFrom(ownerPublicKeyBytes)))
        ).build();
    }

    public static AddAuthenticatedDataRequest fromProto(bisq.network.protobuf.AddAuthenticatedDataRequest proto) {
        byte[] ownerPublicKeyBytes = proto.getOwnerPublicKeyBytes().toByteArray();
        try {
            PublicKey ownerPublicKey = KeyGeneration.generatePublic(ownerPublicKeyBytes);
            return new AddAuthenticatedDataRequest(
                    AuthenticatedSequentialData.fromProto(proto.getAuthenticatedSequentialData()),
                    proto.getSignature().toByteArray(),
                    ownerPublicKeyBytes,
                    ownerPublicKey
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getCostFactor() {
        DistributedData distributedData = authenticatedSequentialData.getAuthenticatedData().getDistributedData();
        double metaDataImpact = distributedData.getMetaData().getCostFactor();
        double dataImpact = distributedData.getCostFactor();
        double impact = metaDataImpact + dataImpact;
        return MathUtils.bounded(0.25, 0.75, impact);
    }

    public boolean isSignatureInvalid() {
        try {
            return !SignatureUtil.verify(authenticatedSequentialData.serialize(), signature, getOwnerPublicKey());
        } catch (Exception e) {
            log.warn(e.toString(), e);
            return true;
        }
    }

    public boolean isPublicKeyInvalid() {
        try {
            return !Arrays.equals(authenticatedSequentialData.getPubKeyHash(), DigestUtil.hash(ownerPublicKeyBytes));
        } catch (Exception e) {
            return true;
        }
    }

    public PublicKey getOwnerPublicKey() {
        return Optional.ofNullable(ownerPublicKey).orElseGet(() -> {
            try {
                return KeyGeneration.generatePublic(ownerPublicKeyBytes);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getSequenceNumber() {
        return authenticatedSequentialData.getSequenceNumber();
    }

    @Override
    public long getCreated() {
        return authenticatedSequentialData.getCreated();
    }

    @Override
    public int getMaxMapSize() {
        return authenticatedSequentialData.getAuthenticatedData().getMetaData().getMaxMapSize();
    }

    @Override
    public boolean isExpired() {
        return authenticatedSequentialData.isExpired();
    }

    @Override
    public String toString() {
        return "AddAuthenticatedDataRequest{" +
                "\r\n     signature=" + Hex.encode(signature) +
                ",\r\n     ownerPublicKeyBytes=" + Hex.encode(ownerPublicKeyBytes) +
                ", \r\n     authenticatedSequentialData=" + authenticatedSequentialData +
                "\r\n}";
    }
}
