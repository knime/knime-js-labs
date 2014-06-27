/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
org_knime_js_base_node_quickform_input_dbl = function() {
	var doubleInput = {
			version: "1.0.0"
	};
	doubleInput.name = "Double input";
	var viewValue;
	var viewRepresentation;
	var input;
	var errorMessage;

	doubleInput.init = function(representation, value) {
		if (checkMissingData(representation, value)) {
			return;
		}
		viewValue = value;
		viewRepresentation = representation;
		var body = $('body');
		var qfdiv = $('<div class="quickformcontainer">');
		body.append(qfdiv);
		input = $('<input>');
		qfdiv.attr("title", representation.description);
		qfdiv.append('<div class="label">' + representation.label + '</div>');
		qfdiv.append(input);
		input.spinner({
			step: 0.01
		});
		if (viewRepresentation.usemin) {
			input.spinner('option', 'min', viewRepresentation.min);
		}
		if (viewRepresentation.usemax) {
			input.spinner('option', 'max', viewRepresentation.max);
		}
		var doubleValue = value.double;
		input.val(doubleValue);
		qfdiv.append($('<br>'));
		errorMessage = $('<span>');
		errorMessage.css('display', 'none');
		errorMessage.css('color', 'red');
		errorMessage.css('font-style', 'italic');
		errorMessage.css('font-size', '75%');
		qfdiv.append(errorMessage);
		input.blur(callUpdate);
		resizeParent();
		callUpdate();
	};
	
	doubleInput.validate = function() {
		if (!isValid(viewValue) || !isValid(viewRepresentation)) {
			return true;
		}
		var valid;
		var min = viewRepresentation.min;
		var max = viewRepresentation.max;
		var value = input.val();
		if (!$.isNumeric(value)) {
			doubleInput.setValidationErrorMessage('The set value is not a double');
			return false;
		}
		value = parseFloat(value);
		if (viewRepresentation.usemin && value<min) {
			valid = false;
			doubleInput.setValidationErrorMessage("The set double " + value + " is smaller than the allowed minimum of " + min);
		} else if (viewRepresentation.usemax && value>max) {
			valid = false;
			doubleInput.setValidationErrorMessage("The set double " + value + " is bigger than the allowed maximum of " + max);
		} else {
			valid = true;
			doubleInput.setValidationErrorMessage(null);
		}
		return valid;
	};
	
	doubleInput.setValidationErrorMessage = function(message) {
		if (!isValid(viewValue) || !isValid(viewRepresentation)) {
			return;
		}
		if (message != null) {
			errorMessage.text(message);
			errorMessage.css('display', 'inline');
		} else {
			errorMessage.text('');
			errorMessage.css('display', 'none');
		}
		resizeParent();
	};

	doubleInput.value = function() {
		if (!isValid(viewValue)) {
			return null;
		}
		viewValue.double = parseFloat(input.val());
		return viewValue;
	};
	
	return doubleInput;
	
}();
