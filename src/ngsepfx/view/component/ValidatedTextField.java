/*******************************************************************************
 * NGSEP - Next Generation Sequencing Experience Platform
 * Copyright 2016 Jorge Duitama
 *
 * This file is part of NGSEP.
 *
 *     NGSEP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NGSEP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NGSEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ngsepfx.view.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ngsepfx.controller.validator.ValidationEnum;
import ngsepfx.controller.validator.ValidationReport;
import ngsepfx.controller.validator.Validator;

/**
 * @author Fernando Reyes
 *
 */
public class ValidatedTextField extends TextField {
	
	// Attributes.
	
	@FXML
	private ValidationEnum[] validators = new ValidationEnum[0];
	
	@FXML
	private Label label;
	
	// Methods.

	/**
	 * @return the validators
	 */
	public ValidationEnum[] getValidators() {
		return validators;
	}

	/**
	 * @param validators the validators to set
	 */
	public void setValidators(ValidationEnum[] validators) {
		this.validators = validators;
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(Label label) {
		this.label = label;
	}
	
	public ValidationReport validate () {
		return Validator.applyValidations(validators, getText(), label.getText());
	}
	
}
