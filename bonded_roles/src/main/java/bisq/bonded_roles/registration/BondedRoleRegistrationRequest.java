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

package bisq.bonded_roles.registration;

import bisq.bonded_roles.BondedRoleType;
import bisq.common.proto.ProtoResolver;
import bisq.common.proto.UnresolvableProtobufMessageException;
import bisq.common.validation.NetworkDataValidation;
import bisq.network.common.AddressByTransportTypeMap;
import bisq.network.p2p.message.EnvelopePayloadMessage;
import bisq.network.p2p.services.data.storage.MetaData;
import bisq.network.p2p.services.data.storage.mailbox.MailboxMessage;
import bisq.network.identity.NetworkId;
import bisq.network.protobuf.ExternalNetworkMessage;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static bisq.network.p2p.services.data.storage.MetaData.MAX_MAP_SIZE_100;
import static bisq.network.p2p.services.data.storage.MetaData.TTL_10_DAYS;

@Slf4j
@Getter
@ToString
@EqualsAndHashCode
public final class BondedRoleRegistrationRequest implements MailboxMessage {
    private final MetaData metaData = new MetaData(TTL_10_DAYS, getClass().getSimpleName(), MAX_MAP_SIZE_100);
    private final String profileId;
    private final String authorizedPublicKey;
    private final BondedRoleType bondedRoleType;
    private final String bondUserName;
    private final String signatureBase64;
    private final NetworkId networkId;
    private final boolean isCancellationRequest;
    private final AddressByTransportTypeMap addressByTransportTypeMap;

    public BondedRoleRegistrationRequest(String profileId,
                                         String authorizedPublicKey,
                                         BondedRoleType bondedRoleType,
                                         String bondUserName,
                                         String signatureBase64,
                                         AddressByTransportTypeMap addressByTransportTypeMap,
                                         NetworkId networkId,
                                         boolean isCancellationRequest) {
        this.profileId = profileId;
        this.authorizedPublicKey = authorizedPublicKey;
        this.bondedRoleType = bondedRoleType;
        this.bondUserName = bondUserName;
        this.signatureBase64 = signatureBase64;
        this.addressByTransportTypeMap = addressByTransportTypeMap;
        this.networkId = networkId;
        this.isCancellationRequest = isCancellationRequest;

        NetworkDataValidation.validateProfileId(profileId);
        NetworkDataValidation.validatePubKeyHex(authorizedPublicKey);
        NetworkDataValidation.validateBondUserName(bondUserName);
        NetworkDataValidation.validateSignatureBase64(signatureBase64);


        // log.error("{} {}", metaData.getClassName(), toProto().getSerializedSize());//637
    }

    @Override
    public bisq.network.protobuf.EnvelopePayloadMessage toProto() {
        return getNetworkMessageBuilder()
                .setExternalNetworkMessage(ExternalNetworkMessage.newBuilder()
                        .setAny(Any.pack(toAuthorizeRoleRegistrationRequestProto())))
                .build();
    }

    public bisq.bonded_roles.protobuf.BondedRoleRegistrationRequest toAuthorizeRoleRegistrationRequestProto() {
        return bisq.bonded_roles.protobuf.BondedRoleRegistrationRequest.newBuilder()
                .setProfileId(profileId)
                .setAuthorizedPublicKey(authorizedPublicKey)
                .setBondedRoleType(bondedRoleType.toProto())
                .setBondUserName(bondUserName)
                .setSignatureBase64(signatureBase64)
                .setAddressByTransportTypeMap(addressByTransportTypeMap.toProto())
                .setNetworkId(networkId.toProto())
                .setIsCancellationRequest(isCancellationRequest)
                .build();
    }

    public static BondedRoleRegistrationRequest fromProto(bisq.bonded_roles.protobuf.BondedRoleRegistrationRequest proto) {
        return new BondedRoleRegistrationRequest(proto.getProfileId(),
                proto.getAuthorizedPublicKey(),
                BondedRoleType.fromProto(proto.getBondedRoleType()),
                proto.getBondUserName(),
                proto.getSignatureBase64(),
                AddressByTransportTypeMap.fromProto(proto.getAddressByTransportTypeMap()),
                NetworkId.fromProto(proto.getNetworkId()),
                proto.getIsCancellationRequest());
    }

    public static ProtoResolver<EnvelopePayloadMessage> getNetworkMessageResolver() {
        return any -> {
            try {
                bisq.bonded_roles.protobuf.BondedRoleRegistrationRequest proto = any.unpack(bisq.bonded_roles.protobuf.BondedRoleRegistrationRequest.class);
                return BondedRoleRegistrationRequest.fromProto(proto);
            } catch (InvalidProtocolBufferException e) {
                throw new UnresolvableProtobufMessageException(e);
            }
        };
    }

    @Override
    public double getCostFactor() {
        return getCostFactor(0.5, 1);
    }
}