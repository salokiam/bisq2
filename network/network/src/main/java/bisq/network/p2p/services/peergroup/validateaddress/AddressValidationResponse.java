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

package bisq.network.p2p.services.peergroup.validateaddress;

import bisq.network.p2p.message.EnvelopePayloadMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class AddressValidationResponse implements EnvelopePayloadMessage {
    private final int requestNonce;

    public AddressValidationResponse(int requestNonce) {
        this.requestNonce = requestNonce;
    }

    @Override
    public bisq.network.protobuf.EnvelopePayloadMessage toProto() {
        return getNetworkMessageBuilder().setAddressValidationResponse(
                        bisq.network.protobuf.AddressValidationResponse.newBuilder()
                                .setRequestNonce(requestNonce))
                .build();
    }

    public static AddressValidationResponse fromProto(bisq.network.protobuf.AddressValidationResponse proto) {
        return new AddressValidationResponse(proto.getRequestNonce());
    }
}