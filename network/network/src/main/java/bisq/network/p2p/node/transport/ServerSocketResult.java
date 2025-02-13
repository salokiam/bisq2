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

package bisq.network.p2p.node.transport;

import bisq.network.common.Address;
import bisq.network.identity.TorIdentity;
import bisq.tor.onionservice.CreateOnionServiceResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.net.ServerSocket;

@Getter
@ToString
@EqualsAndHashCode
public final class ServerSocketResult {
    private final TorIdentity torIdentity;
    private final ServerSocket serverSocket;
    private final Address address;

    public ServerSocketResult(TorIdentity torIdentity, ServerSocket serverSocket, Address address) {
        this.torIdentity = torIdentity;
        this.serverSocket = serverSocket;
        this.address = address;
    }

    public ServerSocketResult(CreateOnionServiceResponse response) {
        this(response.getTorIdentity(), response.getServerSocket(), new Address(response.getOnionAddress().toString()));
    }
}