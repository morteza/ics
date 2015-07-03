/*******************************************************************************
 *        File: Configuration.java
 *    Revision: 2
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 4, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.data.validation.Unique;

@Entity
public class Configuration extends ModelWithTimestamp {
    
    @Unique
    @Required
    public String configKey;
    
    @Required
    public String configValue;
}
