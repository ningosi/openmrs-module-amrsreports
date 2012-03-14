package org.openmrs.module.amrsreport.rule.medication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

public class MohCTXStoptTreatmentRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohCTXStartTreatmentRule.class);
	 
 	public static final String TOKEN = "MOH CTX Stop date";
 	
 	private Map<String, Concept> cachedConcepts = null;
	private List<Concept> cachedQuestions = null;
	private List<Concept> cachedAnswers = null;

	/**
	 * comparator for sorting observations
	 */
	private static class SortByDateComparator implements Comparator<Object>{

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getObsDatetime().compareTo(bo.getObsDatetime());
		}
	}
 	/**

	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
 	 */
			//get ctx stop date
	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		
		Result result=new Result();
		
		Date ctxStop=null;
		Result ctxStopResult=null;
		//find the patient
		Patient patient = Context.getPatientService().getPatient(patientId);
		//find the obs for this patient by the concept question
		List<Obs> obs=Context.getObsService().getObservations(
				Arrays.asList(new Person[]{patient}), null, getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);
				
		
		
		
		Collections.sort(obs,new SortByDateComparator());
		for (Obs o : obs) {
			List<Concept> answerList=getCachedAnswers();
			
			for(Concept obans:answerList){
				
				if((o.getValueCoded().getConceptId())==(obans.getConceptId())){
					
					ctxStop=o.getObsDatetime();
					ctxStopResult = new Result(ctxStop);
					result.add(ctxStopResult);
					
				}
			}
			
		}
		
			
		
		return result;
}

	protected String getEvaluableToken() {
		return TOKEN;
 	}
	

	
 	/**
 	 * @see org.openmrs.logic.Rule#getDependencies()
 	 */
	@Override
 	public String[] getDependencies() {
		return new String[]{};
 	}
 	
	
 	@Override
	public Datatype getDefaultDatatype() {
		// TODO Auto-generated method stub
		return Datatype.DATETIME;
 	}
	public Set<RuleParameterInfo> getParameterList() {
		// TODO Auto-generated method stub
		return null;
 	}
	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Concept getCachedConcept(String name) {
		
		if (cachedConcepts == null) {
			cachedConcepts = new HashMap<String, Concept>();
		}
		if (!cachedConcepts.containsKey(name)) {
			cachedConcepts.put(name, Context.getConceptService().getConcept(name));
		}
		return cachedConcepts.get(name);
		
	}
	
	private List<Concept> getQuestionConcepts() {
		if (cachedQuestions == null) {
			cachedQuestions = new ArrayList<Concept>();
			cachedQuestions.add(getCachedConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED));
		}
		return cachedQuestions;
	}	
	private List<Concept> getCachedAnswers() {
		if (cachedAnswers == null) {
			cachedAnswers = new ArrayList<Concept>();
			cachedAnswers.add(getCachedConcept(MohEvaluableNameConstants.CD4_COUNT_GREATER_THAN_15));
			cachedAnswers.add(getCachedConcept(MohEvaluableNameConstants.WEIGHT_CHANGE));
			cachedAnswers.add(getCachedConcept(MohEvaluableNameConstants.CD4_COUNT_GREATER_THAN_200));
			cachedAnswers.add(getCachedConcept(MohEvaluableNameConstants.TOXICITY_DRUG));
			cachedAnswers.add(getCachedConcept(MohEvaluableNameConstants.OTHER_NON_CODED));
		}
		return cachedAnswers;
	}	
	

}