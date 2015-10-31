/*******************************************************************************
 *        File: CVSS.java
 *    Revision: 1
 * Description: CVSS 3.0 Controller
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 6, 2014
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.util.HashMap;
import java.util.Map;

import play.i18n.Messages;
import play.mvc.Controller;

public class CVSS extends Controller {
  public static void calculator() {
    render("cvss/calculator.html");
  }
  
  public static void calculateCVSSFromMetrics() {
    String AV = request.params.get("AV");
    String AC = request.params.get("AC");
    String PR = request.params.get("PR");
    String UI = request.params.get("UI");
    String S = request.params.get("S");
    String C = request.params.get("C");
    String I = request.params.get("I");
    String A = request.params.get("A");
    
    Map<String, Double> weights = getWeights(S);
    
    double wAV=0, wAC=0, wPR=0, wUI=0, wS=0, wC=0, wI=0, wA=0;
    
    try {
      wAV = weights.get("AV_"+AV);
      wAC = weights.get("AC_"+AC);
      wPR = weights.get("PR_"+PR);
      wUI = weights.get("UI_"+UI);
      wS = weights.get("S_"+S);
      wC = weights.get("CIA_"+C);
      wI = weights.get("CIA_"+I);
      wA = weights.get("CIA_"+A);      
    } catch(Exception e) {
      renderText(Messages.get("None"));
    }

    
    String CVSSVersionIdentifier = "CVSS:3.0";
    double exploitabilityCoefficient = 8.22;
    double scopeCoefficient = 1.08;
    
    double baseScore = 0.0;
    double impactSubScore;
    double exploitabalitySubScore = exploitabilityCoefficient * wAV * wAC * wPR * wUI;
    double impactSubScoreMultiplier = (1 - ((1 - wC) * (1 - wI) * (1 - wA)));

    if ("U".equals(S)) {
      impactSubScore = wS * impactSubScoreMultiplier;
    } else {
      impactSubScore = wS * (impactSubScoreMultiplier - 0.029) - 3.25 * Math.pow(impactSubScoreMultiplier - 0.02, 15);
    }

    if (impactSubScore <= 0) {
      baseScore = 0.0;
    } else {
      if ("U".equals(S)) {
        baseScore = roundUp1(Math.min((exploitabalitySubScore + impactSubScore), 10));
      } else {
        baseScore = roundUp1(Math.min((exploitabalitySubScore + impactSubScore) * scopeCoefficient, 10));
      }
    }

    renderText(baseScore + "");
  }

  /** Severity rating bands, as defined in the CVSS v3.0 specification.
   * 
   * @param score
   */
  public static void severityRating(double score) {
    if (score==0.0) {
      renderText(Messages.get("None"));
    } else if (score>0.0 && score<4.0) {
      renderText(Messages.get("Low"));
    } else if (score>=4.0 && score<7.0) {
      renderText(Messages.get("Medium"));
    } else if (score>=7.0 && score<9) {
      renderText(Messages.get("High"));
    } else if (score>=9.0 && score <=10.0) {
      renderText(Messages.get("Critical"));
    }
    renderText(Messages.get("None"));
  }
  
  public static Map<String, Double> getWeights(String S) {
    Map<String, Double> weights = new HashMap<String, Double>();
    
    weights.put("AV_N", 0.85);
    weights.put("AV_A", 0.62);
    weights.put("AV_L", 0.55);
    weights.put("AV_P", 0.2);

    weights.put("AC_H", 0.44);
    weights.put("AC_L", 0.77);

    weights.put("PR_N", 0.85);

    // Changed or Unchanged Scope metric defines PR weights.
    if ("U".equalsIgnoreCase(S)) {
      weights.put("PR_L", 0.62);
      weights.put("PR_H", 0.27);
    } else {
      weights.put("PR_L", 0.68);
      weights.put("PR_H", 0.5);      
    }
    
    weights.put("UI_N", 0.85);
    weights.put("UI_R", 0.62);

    weights.put("S_U", 6.42);
    weights.put("S_C", 7.52);

    weights.put("CIA_N", 0.0);
    weights.put("CIA_L", 0.22);
    weights.put("CIA_H", 0.56);

    return weights;
  }
  
  public static double roundUp1(double d) {
    return Math.ceil(d * 10) / 10;
  }
}
