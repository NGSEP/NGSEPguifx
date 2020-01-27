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
package ngsepfx.controller.validator;

import java.util.ArrayList;

/**
 * @author Fernando Reyes
 *
 */
public class ValidationReport {
	
	// Attributes.
	
	private String label;
	
	private ArrayList<String> errors;
	
	//Consructors
	
	public ValidationReport(String label) {
		this.label = label;
		this.errors = new ArrayList<>();
	}
	
	public String getLabel() {
		return label;
	}

	/**
	 * @return the errors
	 */
	public ArrayList<String> getErrors() {
		return errors;
	}
	
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	// Methods

	public void addError(String error) {
		errors.add(error);
	}
	public String toHierarchichalString() {
		String errorsString = "";
		errorsString += getLabel() + System.lineSeparator();
		for (String error : getErrors()) {
			errorsString += "\t" + error + System.lineSeparator();
		}
		return errorsString;
	}
}
