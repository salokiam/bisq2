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

package bisq.desktop.common.utils.validation;


import bisq.i18n.Res;

public class PasswordValidator extends InputValidator {
    public ValidationResult validate(CharSequence value) {
        //todo trim
        if (value == null || value.isEmpty()) {
            return new ValidationResult(false, Res.get("validation.empty"));
        }

        if (value.length() < 8) {
            return new ValidationResult(false, Res.get("validation.password.tooShort"));
        }

        return new ValidationResult(true);
    }

    public ValidationResult validate(CharSequence password1, CharSequence password2) {
        ValidationResult result = validate(password1);
        if (!result.isValid) {
            return result;
        }

        if (!password1.equals(password2)) {
            return new ValidationResult(false, Res.get("validation.password.notMatching"));
        }
        return new ValidationResult(true);
    }

    public void setError() {

    }
}
