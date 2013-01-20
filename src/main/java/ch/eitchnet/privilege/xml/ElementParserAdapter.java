/*
 * Copyright (c) 2012, Robert von Burg
 *
 * All rights reserved.
 *
 * This file is part of the Privilege.
 *
 *  Privilege is free software: you can redistribute 
 *  it and/or modify it under the terms of the GNU General Public License as 
 *  published by the Free Software Foundation, either version 3 of the License, 
 *  or (at your option) any later version.
 *
 *  Privilege is distributed in the hope that it will 
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Privilege.  If not, see 
 *  <http://www.gnu.org/licenses/>.
 */
package ch.eitchnet.privilege.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class ElementParserAdapter implements ElementParser {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// empty implementation
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// empty implementation
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// empty implementation
	}

	@Override
	public void notifyChild(ElementParser child) {
		// empty implementation
	}
}