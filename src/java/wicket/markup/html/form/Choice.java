/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.model.IModel;

/**
 * Abstract base class for all Choice (html select) options.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 * @author Johan Compagner
 */
abstract class Choice extends FormComponent
{
	/** Serial Version ID. */
	private static final long serialVersionUID = -8334966481181600604L;

	/**
	 * Default value to display when a null option is rendered. Initially set to
	 * 'Choose One'.
	 */
	private static final String DEFAULT_NULL_OPTION_VALUE = "Choose One";

	/** Whether the null option must be rendered if current selection == null. */
	private boolean renderNullOption = true;

	/** The list of values. */
	private List values;

	/**
	 * @param name
	 *            See Component constructor
	 * @param model
	 *            See Component constructor
	 * @param values
	 *            The collection of values in the dropdown
	 * @see wicket.Component#Component(String, IModel)
	 */
	public Choice(String name, IModel model, final Collection values)
	{
		super(name, model);
		setValues(values);
	}

	/**
	 * @param name
	 *            See Component constructor
	 * @param model
	 *            See Component constructor
	 * @param expression
	 *            See Component constructor
	 * @param values
	 *            The collection of values in the dropdown
	 * @see wicket.Component#Component(String, IModel, String)
	 */
	public Choice(String name, IModel model, String expression,
			final Collection values)
	{
		super(name, model, expression);
		setValues(values);
	}

	/**
	 * @param name
	 *            See Component constructor
	 * @param object
	 *            See Component constructor
	 * @param values
	 *            The collection of values in the dropdown
	 * @see wicket.Component#Component(String, Serializable)
	 */
	public Choice(String name, Serializable object, final Collection values)
	{
		super(name, object);
		setValues(values);
	}

	/**
	 * @param name
	 *            See Component constructor
	 * @param object
	 *            See Component constructor
	 * @param expression
	 *            See Component constructor
	 * @param values
	 *            The collection of values in the dropdown
	 * @see wicket.Component#Component(String, Serializable, String)
	 */
	public Choice(String name, Serializable object, String expression,
			final Collection values)
	{
		super(name, object, expression);
		setValues(values);
	}

    /**
     * @see wicket.markup.html.form.FormComponent#supportsPersistence()
     */
    public boolean supportsPersistence()
    {
        return true;
    }

    /**
	 * Gets the list of values.
	 * 
	 * @return the list of values
	 */
	public List getValues()
	{
		if (values instanceof IIdList)
		{
			((IIdList)values).attach();
		}
		return this.values;
	}
    
	/**
	 * Gets whether the null option must be rendered if current selection ==
	 * null. The default is true.
	 * 
	 * @return boolean
	 */
	public boolean isRenderNullOption()
	{
		return renderNullOption;
	}

	/**
	 * Sets whether the null option must be rendered if current selection ==
	 * null.
	 * 
	 * @param renderNullOption
	 *            whether the null option must be rendered if current selection ==
	 *            null.
	 */
	public void setRenderNullOption(boolean renderNullOption)
	{
		this.renderNullOption = renderNullOption;
	}

	/**
	 * Sets the values to use for the dropdown.
	 * 
	 * @param values
	 *            values to set
	 * @return dropdown choice
	 */
	public Choice setValues(final Collection values)
	{
		if (values == null)
		{
			this.values = Collections.EMPTY_LIST;
		}
		else if (values instanceof List)
		{
			this.values = (List)values;
		}
		else
		{
			this.values = new ArrayList(values);
		}
		return this;
	}

	/**
	 * Updates the model of this component from the request.
	 * 
	 * @see wicket.markup.html.form.FormComponent#updateModel()
	 */
	public abstract void updateModel();

	/**
	 * Handle the container's body.
	 * 
	 * @param markupStream
	 *            The markup stream
	 * @param openTag
	 *            The open tag for the body
	 * @see wicket.Component#handleBody(MarkupStream, ComponentTag)
	 */
	protected final void handleBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		final StringBuffer options = new StringBuffer();
		final Object selected = getModelObject();
		final List list = getValues();

		if (selected == null && isRenderNullOption())
		{
			final String chooseOne = getLocalizer().getString(getName() + ".null", this,
					DEFAULT_NULL_OPTION_VALUE);

			options.append("\n<option selected value=\"").append("\">").append(chooseOne).append(
					"</option>");
		}

		for (int i = 0; i < list.size(); i++)
		{
			final Object value = list.get(i);

			if (value != null)
			{
				final String idValue;
				final String displayValue;
				if (list instanceof IIdList)
				{
					IIdList idList = (IIdList)list;
					idValue = idList.getIdValue(i);
					displayValue = idList.getDisplayValue(i);

				}
				else
				{
					idValue = Integer.toString(i);
					displayValue = value.toString();
				}
				final boolean currentOptionIsSelected = isSelected(value);
				options.append("\n<option ");
				if (currentOptionIsSelected)
					options.append("selected ");
				options.append("value=\"");
				options.append(idValue);
				options.append("\">");
				options.append(getLocalizer().getString(getName() + "." + displayValue, this,
						displayValue));
				options.append("</option>");
			}
			else
			{
				throw new IllegalArgumentException(
						"Dropdown choice contains null value in values collection at index " + i);
			}
		}

		options.append("\n");
		replaceBody(markupStream, openTag, options.toString());

		// Deattach the list after this. Check if this is the right place!
		if (list instanceof IIdList)
		{
			((IIdList)list).detach();
		}
	}

	/**
	 * Processes the component tag.
	 * 
	 * @param tag
	 *            Tag to modify
	 * @see wicket.Component#handleComponentTag(wicket.markup.ComponentTag)
	 */
	protected void handleComponentTag(final ComponentTag tag)
	{
		checkTag(tag, "select");
		super.handleComponentTag(tag);
	}

	/**
	 * Gets whether the given value represents the current selection.
	 * 
	 * @param currentValue
	 *            the current list value
	 * @return whether the given value represents the current selection
	 */
	protected boolean isSelected(Object currentValue)
	{
		Object modelObject = getModelObject();
		if (modelObject == null)
		{
			if (currentValue == null)
				return true;
			else
				return false;
		}
		boolean equals = currentValue.equals(modelObject);
		return equals;
	}
}