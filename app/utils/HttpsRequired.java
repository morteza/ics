/*******************************************************************************
 *        File: HttpsRequired.java
 *    Revision: 3
 * Description: Force secure connection via https instead of http.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Sep 24, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package utils;

import play.Play;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * Forces all actions of desired controller to use secure https instead of http.
 * Use this class with <code>@With(HttpsRequired)</code> annotation on controller.
 *
 */
public class HttpsRequired extends Controller {
  /**
   * Called before every request to ensure that HTTPS is used in production mode.
   */
  @Before
  public static void redirectToHttps() {
    //if it's not secure, but Heroku has already done the SSL processing then it might actually be secure after all
    if (!request.secure && request.headers.get("x-forwarded-proto") != null) {
      request.secure = request.headers.get("x-forwarded-proto").values.contains("https");
    }

    //redirect if it's not secure and app is not in dev mode.
    if (!request.secure) {
      if (Play.mode.isDev())
        redirect("http://" + request.host + request.url);
      else
        redirect("https://" + request.host + request.url);
    }
  }
   
}
