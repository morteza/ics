/*******************************************************************************
 *        File: NoSecurityCheck.java
 *    Revision: 3
 * Description: Annotation to avoid security check on an action, or
 *              whole controller and all its actions.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: October 27, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prevents security check on an action or all actions of a controller.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NoSecurityCheck {

}
