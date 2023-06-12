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

package bisq.offer.payment_method;

import bisq.account.payment_method.BitcoinPaymentMethod;
import bisq.account.payment_method.BitcoinPaymentRail;
import bisq.account.payment_method.FiatPaymentMethod;
import bisq.account.payment_method.PaymentMethod;
import bisq.offer.Offer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class PaymentMethodSpecUtil {
    public static Optional<FiatPaymentMethodSpec> findFiatPaymentMethodSpec(PaymentMethodSpec paymentMethodSpec) {
        return paymentMethodSpec instanceof FiatPaymentMethodSpec ?
                Optional.of((FiatPaymentMethodSpec) paymentMethodSpec) :
                Optional.empty();
    }

    public static Optional<BitcoinPaymentMethodSpec> findBitcoinPaymentMethodSpec(PaymentMethodSpec paymentMethodSpec) {
        return paymentMethodSpec instanceof BitcoinPaymentMethodSpec ?
                Optional.of((BitcoinPaymentMethodSpec) paymentMethodSpec) :
                Optional.empty();
    }

    public static List<BitcoinPaymentMethodSpec> createBitcoinPaymentMethodSpecs(List<BitcoinPaymentRail> bitcoinPaymentRails) {
        return bitcoinPaymentRails.stream()
                .map(BitcoinPaymentMethod::fromPaymentRail)
                .map(BitcoinPaymentMethodSpec::new)
                .collect(Collectors.toList());
    }

    public static List<BitcoinPaymentMethodSpec> createBitcoinMainChainPaymentMethodSpec() {
        return createBitcoinPaymentMethodSpecs(List.of(BitcoinPaymentRail.MAIN_CHAIN));
    }

    public static List<FiatPaymentMethodSpec> createFiatPaymentMethodSpecs(List<FiatPaymentMethod> paymentMethods) {
        checkArgument(!paymentMethods.isEmpty());
        return paymentMethods.stream()
                .map(FiatPaymentMethodSpec::new)
                .collect(Collectors.toList());
    }

    public static <M extends PaymentMethod<?>, T extends PaymentMethodSpec<M>> List<M> getPaymentMethods(Collection<T> paymentMethodSpecs) {
        return paymentMethodSpecs.stream()
                .map(PaymentMethodSpec::getPaymentMethod)
                .collect(Collectors.toList());
    }

    public static <M extends PaymentMethod<?>, T extends PaymentMethodSpec<M>> List<String> getPaymentMethodNames(Collection<T> paymentMethodSpecs) {
        return getPaymentMethods(paymentMethodSpecs).stream()
                .map(PaymentMethod::getName)
                .collect(Collectors.toList());
    }

    public static <M extends PaymentMethod<?>, T extends PaymentMethodSpec<M>> List<String> getBaseSidePaymentMethodNames(Offer<T, ?> offer) {
        return getPaymentMethodNames(offer.getBaseSidePaymentMethodSpecs());
    }

    public static <M extends PaymentMethod<?>, T extends PaymentMethodSpec<M>> List<String> getQuoteSidePaymentMethodNames(Offer<?, T> offer) {
        return getPaymentMethodNames(offer.getQuoteSidePaymentMethodSpecs());
    }
}