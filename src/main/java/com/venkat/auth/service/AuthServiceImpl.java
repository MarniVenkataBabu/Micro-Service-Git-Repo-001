package com.ksi.ep.web.eao.action.requisition.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ksi.ep.common.core.constants.EpConstants;
import com.ksi.ep.common.eao.constants.EpEaoConstants;
import com.ksi.ep.common.pms.util.EpUtil;
import com.ksi.ep.common.core.util.SessionUtil;
import com.ksi.ep.dao.core.model.EpUsr;
import com.ksi.ep.service.core.ManagerFactory;
import com.ksi.ep.service.eao.RequisitionManager;
import com.ksi.ep.service.eao.RequisitionSpeciesManager;
import com.ksi.ep.service.eao.impl.EAOMailManagerImpl;
import com.ksi.ep.web.core.action.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Locale;
import org.springframework.web.bind.annotation.RequestMapping;
import  org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CancelReqnSpeciesAction extends BaseAction {

	//private static Log log = LogFactory.getLog(CancelReqnSpeciesAction.class);
	private static final Logger log = LoggerFactory.getLogger(CancelReqnSpeciesAction.class);

	@RequestMapping(value="/cancelReqnSpecies", method =  RequestMethod.POST)
	public String cancelReqnSpecies(HttpServletRequest request,HttpServletResponse response,
			Model model,Locale loc)
			throws Exception {
		log.info("Controller - CancelReqnSpeciesAction.cancelReqnSpecies() START");
		RequisitionManager requisitionManager = null;
		
		EpUsr epUsr = null;
		
		int[] reqItemID=new int[1];
		
		int requisitionId = 0;

		String siteId  = null;
		
		String lineItemsIds[]  = null;
		 log.info("Controller - Entering cancelReqnSpecies()...");
		try {
			populateMDC(request);
			super.checkSession(request,getMapping(request));
			
			epUsr = SessionUtil.getUsr(request);
			
			siteId =  (String) SessionUtil.getAttribute(
					           request, EpConstants.USR_SITE);
			
			int customerId = Integer.parseInt(SessionUtil.getCustomerId(request));
 
			requisitionManager = (RequisitionManager) ManagerFactory
					.getManagerInstance(EpEaoConstants.REQUISITION_MANAGER_BEAN);
			
			RequisitionSpeciesManager requisitionSpeciesManager=(RequisitionSpeciesManager)ManagerFactory.getManagerInstance(EpEaoConstants.REQUISITION_SPECIES_MANAGER_BEAN);
			log.debug("Controller - RequisitionSpeciesManager initialized: {}", requisitionSpeciesManager);

			boolean preFlag = requisitionManager.checkCancelReqPreConditions(request);
			 
			List<Integer> reqnItemIdList =null;
			String reqnItemIds="";
			HashMap <Integer, String> cancelReasonsMap= new HashMap<>(); 

			if(request.getParameter("reqId") != null && 
					   !"".equals(request.getParameter("reqId"))){
				
				requisitionId = Integer.parseInt(request.getParameter("reqId"));
				if (requisitionId > 0) {
				    MDC.put("reqId", String.valueOf(requisitionId));
				}
				log.info("Cancel requisition line item request received");
			}
			
			if(preFlag){
			
				log.debug("Cancellation pre-condition check completed | result={}", preFlag);

                reqnItemIdList = new ArrayList<Integer>();
				
				if(request.getParameter("reqItemID") != null) 
				{
					

					lineItemsIds = request.getParameterValues("reqItemID");
					reqItemID = new int[lineItemsIds.length];
					if (lineItemsIds != null) {
						log.debug("Line item IDs requested for cancellation | items={}", Arrays.toString(lineItemsIds));

					} else {
						log.warn("Cancellation request received without any line item IDs");
					}
					for(int indx=0;indx<lineItemsIds.length;indx++) 
					{												
					  reqItemID[indx] = Integer.parseInt(lineItemsIds[indx]);
					  
					  if(!"".equals(reqnItemIds)){
						 
						  reqnItemIds = reqnItemIds + reqItemID[indx]+"','";	
					  }else{
						  
						  reqnItemIds = reqnItemIds + reqItemID[indx];  
					  }
					  cancelReasonsMap.put(reqItemID[indx],EpUtil.isStringNotEmpty(request.getParameter("cancel"+ reqItemID[indx]))?request.getParameter("cancel"+ reqItemID[indx]).trim().replace("\r\n", "\n"):"");
					  reqnItemIdList.add(Integer.parseInt(lineItemsIds[indx]));
					}
				}
			}
			log.info("Initiating requisition line item cancellation");

			requisitionSpeciesManager.cancelReqnLineItems(requisitionId, lineItemsIds, siteId, 
					             epUsr.getId().getUsrId(), customerId, cancelReasonsMap);
			
			log.info("Requisition line items cancelled successfully");
			
			Object[] reqLineItemIds = (null != reqnItemIdList && !reqnItemIdList.isEmpty()) ? reqnItemIdList.toArray() : null;
			
			/*To Send mail when lineItems are deleted from the Requisition */
			
			EAOMailManagerImpl eAOMailManagerImpl = new EAOMailManagerImpl();
			
			boolean mailflag = eAOMailManagerImpl.saveEAOEmailLog(
					EpEaoConstants.ANIMAL_REQUISITION_CANCEL_MAIL_TEMPLATE, requisitionId,
					EpEaoConstants.MAIL_REQUISITION_TYPE, Integer.parseInt(siteId), null,
					null, null, epUsr.getId().getUsrId(), epUsr.getId().getCustomerId().intValue(), reqLineItemIds);
				if (mailflag) {
					log.debug("Cancellation email logged successfully LineItems={}",Arrays.toString(lineItemsIds));
				} else {
					log.error("Cancellation email failed | LineItems={}",Arrays.toString(lineItemsIds));
				}
			
			if (!mailflag) {

				log.error("Requisition LineItem cancel mail failed for reqId---"
						+ requisitionId+" LineItem No---"+Arrays.toString(lineItemsIds));
			}
			log.info("Cancel requisition line item request processing completed");

			return "forward:/getReqnListForCancellation.do";

		} catch (Exception e) {

			//e.printStackTrace();
			log.error("Unexpected error occurred during requisition line item cancellation", e);


throw e;
		}
		finally {
		    MDC.clear();
		}
	}

}
