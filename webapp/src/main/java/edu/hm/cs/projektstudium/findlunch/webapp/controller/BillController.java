package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Bill;
import edu.hm.cs.projektstudium.findlunch.webapp.model.BillList;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DonationPerMonth;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BillRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DonationPerMonthRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;


/**
 * The class is responsible for handling http calls related to the process of manage the bills.
 */
@Controller
public class BillController {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(BillController.class);
	
	/** The donationPerMonth repository. */
	@Autowired
	private DonationPerMonthRepository donationPerMonthRepository;
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/** The bill repository. */
	@Autowired
	private BillRepository billRepository;
	
	/**
	 * Get the page for showing the bills.
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal Currently logged in user
	 * @param request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/bill" }, method = RequestMethod.GET)
	public String getDonationAndBill(Model model, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " dont have a restaurant. Redirect to /restaurant/add"));
			return "redirect:/restaurant/add";
		}
	
		Restaurant restaurant = restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId());
		ArrayList<Bill> bills = (ArrayList<Bill>) billRepository.findByRestaurantId(restaurant.getId());
		BillList billList = new BillList();
		billList.setBills(bills);
		model.addAttribute("billsWrapper",billList);
		model.addAttribute("donationPerMonth", getDonationOfRestaurant(restaurant));
		
		return "bill";
	}
	
	/**
	 * Get the bill as pdf.
	 * @param billId Id of the bill
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @param response response to display the pdf
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/bill/{billId}", method = RequestMethod.GET)
	public String getPdf(@PathVariable("billId") Integer billId, Model model, Principal principal, HttpServletRequest request, HttpServletResponse response){ //
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " dont have a restaurant. Redirect to /restaurant/add"));
			return "redirect:/restaurant/add";
		}
		Bill neededBill = billRepository.findOne(billId);
		if(neededBill == null){
			return "redirect:/bill?notExistingBill";
		}
		if(authenticatedUser.getAdministratedRestaurant().getId() == neededBill.getRestaurant().getId()){
			response.setContentType("application/pdf");
		    response.setHeader("Content-Disposition", "inline; filename=\""+neededBill.getBillNumber()+".pdf\""); //attachment
			response.setContentLength(neededBill.getBillPdf().length);
			try {
				OutputStream os = response.getOutputStream();
				os.write(neededBill.getBillPdf());
				os.flush();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		return "redirect:/bill?accessDenied";
	}
	
	/**
	 * 
	 * @param donation the new donation
	 * @param bindingResult Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/bill", method = RequestMethod.POST, params={"newDonation"})
	public String changeDonationAmount(@Valid final DonationPerMonth donation, BindingResult bindingResult, Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		Restaurant restaurant = restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId());
		
		ArrayList<Bill> bills = (ArrayList<Bill>) billRepository.findByRestaurantId(restaurant.getId());
		BillList billList = new BillList();
		billList.setBills(bills);
		
		if (bindingResult.hasErrors()) {
			LOGGER.error(LogUtils.getValidationErrorString(request, bindingResult, Thread.currentThread().getStackTrace()[1].getMethodName()));
			model.addAttribute("billsWrapper",billList);
			model.addAttribute("donationPerMonth", donation);
			
			return "bill";
		}
		
		DonationPerMonth currentDonation = getCurrentDonation(donation, restaurant);

		model.addAttribute("billsWrapper",billList);
		model.addAttribute("donationPerMonth", currentDonation);

		return "bill";
	}
	
	/**
	 * Gets the current donation of a restaurant.
	 * @param restaurant needed restaurant
	 * @return donationPerMonth of the restaurant
	 */
	private DonationPerMonth getDonationOfRestaurant(Restaurant restaurant){
		DonationPerMonth donation = donationPerMonthRepository.findFirstByRestaurantIdOrderByDateDesc(restaurant.getId());
		if(donation == null){
			//never spend a donation, this is a new Restaurant
			Date date = new Date();
			donation = new DonationPerMonth();
			donation.setAmount(0.0f);
			donation.setDate(date);
			donation.setDatetimeOfUpdate(date);
			donation.setRestaurant(restaurant);
			restaurant.addDonationPerMonth(donation);
		}
		return donation;
	}
	
	/**
	 * Checks if the modified donation is a new donation for this month and update it or otherwise create a new donationPerMonth for it.
	 * @param modifiedDonation 
	 * @param restaurant restaurant
	 * @return
	 */
	private DonationPerMonth getCurrentDonation(DonationPerMonth modifiedDonation, Restaurant restaurant){
		//vgl. ob es altes oder neues datum ist. alte wird einem neuen zugewiesen mit dem neuen wert. Das neue wird upgedatet 
		Date startDateOfMonth = getStartOfMonth();
		DonationPerMonth currentDonation = null;
		DonationPerMonth oldDonation = getDonationOfRestaurant(restaurant);
		int value = oldDonation.getDate().compareTo(startDateOfMonth); // -1 is before startofMonth, 0 is equal, 1 is after startOfMonth
		
		//donation was in a older Month
		if(oldDonation != null && value < 0){ //oldDonation.getDate().before(startDateOfMonth)
			DonationPerMonth newDonation = new DonationPerMonth();
			newDonation.setAmount(modifiedDonation.getAmount());
			newDonation.setDate(startDateOfMonth);
			newDonation.setDatetimeOfUpdate(new Date());
			newDonation.setRestaurant(restaurant);
			restaurant.addDonationPerMonth(newDonation);
			currentDonation = newDonation;
		}
		//donation in a new Month
		if(oldDonation != null && value >= 0){//oldDonation.getDate().after(startDateOfMonth)
			oldDonation.setAmount(modifiedDonation.getAmount());
			oldDonation.setDatetimeOfUpdate(new Date());
			currentDonation = oldDonation;
		}
		
		donationPerMonthRepository.save(currentDonation);
		return currentDonation;
	}
	
	/**
	 * Gives the start date of the current Month.
	 * @return Start date of the Month
	 */
	private Date getStartOfMonth(){
		LocalDate now = LocalDate.now();
		LocalDate firstDayOfMonth = now.withDayOfMonth(1);
		Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return startDate;
	}
}
