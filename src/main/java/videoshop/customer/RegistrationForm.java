/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package videoshop.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a so called form-backing class to prepare the form display (in
 * {@link CustomerController#register(org.springframework.ui.Model, RegistrationForm)}) and during form submission (in
 * {@link CustomerController#registerNew(RegistrationForm, Errors)}.
 * <p>
 * The latter will have the form data bound to constructor of this class and the {@link Valid} annotation on the
 * controller method parameter will cause the validation annotation on the fields being triggered. For invalid values,
 * the {@link Errors} instance gets a field error registered and a couple of internationalization codes registered that
 * Spring will try to resolve against the messages declared in {@code messages.properties}.
 * <p>
 * The codes will look as follows:
 * <ul>
 * <li>{@code $SimpleAnnotationName.$SimpleBeanTypeName.$propertyName} - e.g.
 * {@code NotEmpty.registrationForm.name}</li>
 * <li>{@code $SimpleAnnotationName.$propertyName} - e.g. {@code NotEmpty.name}</li>
 * <li>{@code $SimpleAnnotationName.$fullyQualifiedTypeName} - e.g. {@code NotEmpty.java.lang.String}</li>
 * <li>{@code $SimpleAnnotationName} – e.g. {@code NotEmpty}</li>
 * </ul>
 * This allows you to declare generic error messages for based on the problem rejected but also very specific ones for
 * the particular binding type and field.
 * <p>
 * Advanced validations can be implemented by declaring custom validation methods on the form-backing class, evaluating
 * the state and manually rejecting field values (see {@link #validate(Errors)}).
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 */
class RegistrationForm {

	private final @NotEmpty String name, password, address, passwordcheck;

	public RegistrationForm(String name, String password, String address, String passwordcheck) {

		this.name = name;
		this.password = password;
		this.address = address;

        this.passwordcheck = passwordcheck;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getAddress() {
		return address;
	}

	public void validate(Errors errors, CustomerManagement customerManagement) {
		// Complex validation goes here
        if(!this.password.equals(this.passwordcheck)){
            // System.out.println("Error entdeckt");
            errors.rejectValue("passwordcheck", "NotSame.registrationForm.passwordcheck", "The passwords didn't match");
        }
        // customerManagement.findAll().stream().forEach(s->System.out.println(s.getUserAccount().getUsername()));
        // customerManagement.findAll().stream().forEach(s->System.out.println(s.getUserAccount().getUsername().equals(this.name)));
        customerManagement.findAll().stream().forEach(s -> {
            if (s.getUserAccount().getUsername().equals(this.name)) {
                // System.out.println("Den Nutzernamen gibt es schon");
                errors.rejectValue("name", "AlreadyTaken.registrationForm.name", "That name is not availible anymore.");
            }
        });
        if(!this.password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")){
            errors.rejectValue("password", "NotRightFormat.registrationForm.password", "The password must contain at least 8 symbols, one capital letter, one number. No special characters.");
        };
	}

    public String getPasswordcheck() {
        return passwordcheck;
    }
}
