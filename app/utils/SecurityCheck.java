/*******************************************************************************
 *        File: SecurityCheck.java
 *    Revision: 3
 * Description: Check if connected user has specific role.
 *              Used for authentication in <code>Security</code> controller.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import models.AccountRole;

/**
 * Check if connected user has specific role.
 * Used for authentication in <code>Security</code> controller. It can be sued
 * both to annotate a single action or the whole controller and all its actions.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SecurityCheck {

    AccountRole[] value();
}
