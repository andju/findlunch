package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Account;
import edu.hm.cs.projektstudium.findlunch.webapp.model.AccountResult;
import edu.hm.cs.projektstudium.findlunch.webapp.model.AccountType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Bill;
import edu.hm.cs.projektstudium.findlunch.webapp.model.BillList;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Booking;
import edu.hm.cs.projektstudium.findlunch.webapp.model.BookingReason;
import edu.hm.cs.projektstudium.findlunch.webapp.model.BookingResult;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BillRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BookingReasonRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BookingRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;

/**
 * The class is responsible for handling http calls related to the process of manage the bookings for FindLunch.
 */
@Controller
public class BookingController {
	
	/** The booking repository. */
	@Autowired
	private BookingRepository bookingRepository;
	
	/** The bookingReason repository. */
	@Autowired
	private BookingReasonRepository bookingReasonRepository;
	
	/** The accountType repository. */
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	
	/** The account repository. */
	@Autowired
	private AccountRepository accountRepository;
	
	/** The bill repository. */
	@Autowired
	private BillRepository billRepository;
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);
	
	/**
	 * Get the page for showing the booking.
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/booking", method=RequestMethod.GET)//	@PreAuthorize("hasAuthority('Betreiber')")
	public String getBookings(Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		List<Booking> bookings = bookingRepository.findAll();
		float allClaims = getSumOfClaim(bookings);
		float paidClaim = getPaidClaim(bookings);
		BookingResult bookingResult = new BookingResult();
		bookingResult.setAllClaim(allClaims);
		bookingResult.setPaidClaim(paidClaim);
		bookingResult.setNotPaidClaim(round(allClaims - paidClaim, 2));
		LocalDate now = LocalDate.now();
		
		bookingResult.setStartDate(getStartDateOfMonth(now));
		bookingResult.setEndDate(getEndDateOfMonth(now));
		
		ArrayList<Bill> paidBills = (ArrayList<Bill>) billRepository.findByPaidTrue();//findAll();//findByStartDateGreaterThanEqualAndEndDateLessThanEqual(bookingResult.getStartDate(),bookingResult.getEndDate());
		BillList paidBillList = new BillList();
		paidBillList.setBills(paidBills);
		
		ArrayList<Bill> notPaidBills = (ArrayList<Bill>) billRepository.findByPaidFalse();//findAll();//findByStartDateGreaterThanEqualAndEndDateLessThanEqual(bookingResult.getStartDate(),bookingResult.getEndDate());
		
		BillList notPaidBillList = new BillList();
		notPaidBillList.setBills(notPaidBills);
		
		
		model.addAttribute("paidBillsWrapper",paidBillList);
		model.addAttribute("notPaidBillsWrapper",notPaidBillList);
		model.addAttribute("bookingResult", bookingResult);
		model.addAttribute("accountResult", getAccountsResults());
		
		return "booking";
	}
	
	/**
	 * Get the bill as pdf.
	 * @param billNumber bill Number from a bill
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @param response response to display the pdf
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/booking/{billNumber}", method = RequestMethod.GET)//	@PreAuthorize("hasAuthority('Betreiber')")
	public String getPdf(@PathVariable("billNumber") String billNumber, Model model, Principal principal, HttpServletRequest request, HttpServletResponse response){ //
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		Bill neededBill = billRepository.findByBillNumber(billNumber);
		if(neededBill == null){
			return "redirect:/booking?notExistingBill";
		}
		
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
	
	/**
	 * Confirm selected bills.
	 * @param billList List of bills
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/booking", method = RequestMethod.POST, params={"confrim"})//	@PreAuthorize("hasAuthority('Betreiber')")
	public String confirmBill(@ModelAttribute BillList billList, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		List<Bill> bills = billList.getBills();
		List<Bill> confirmdBills = new ArrayList<Bill>();
		
		for(Bill b : bills){
			if(b.isPaid()){
				confirmdBills.add(b);
			}
		}
		List<Restaurant> restaurants = restaurantRepository.findAll();
		
		for(Bill b : confirmdBills){
			Bill bill = billRepository.findOne(b.getId());
			bill.setPaid(true);
			billRepository.save(bill);
			bookingOfDeposit(bill, findAdminsOfRestaurant(restaurants,bill.getRestaurant().getId()));
		}
		
		return "redirect:/booking?success";
	}
	
	/**
	 * Calculate a Claim for a given bookingResult.
	 * @param bookingResult BookingResult
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/booking", method = RequestMethod.POST, params={"claim"})//	@PreAuthorize("hasAuthority('Betreiber')")
	public String calculateClaim(@ModelAttribute BookingResult bookingResult, Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		List<Booking> bookings = bookingRepository.findByBookingTimeBetween(bookingResult.getStartDate(), bookingResult.getEndDate());
		float allClaims = getSumOfClaim(bookings);
		float paidClaim = getPaidClaim(bookings);
		BookingResult newBookingResult = new BookingResult();
		newBookingResult.setAllClaim(allClaims);
		newBookingResult.setPaidClaim(paidClaim);
		newBookingResult.setNotPaidClaim(round(allClaims - paidClaim, 2));
		newBookingResult.setStartDate(bookingResult.getStartDate());
		newBookingResult.setEndDate(bookingResult.getEndDate());
		
		
//		ArrayList<Bill> bills = (ArrayList<Bill>) billRepository.findAll();//findByStartDateGreaterThanEqualAndEndDateLessThanEqual(bookingResult.getStartDate(), bookingResult.getEndDate());
//		BillList billList = new BillList();
//		billList.setBills(bills);
//		model.addAttribute("billsWrapper",billList);
		ArrayList<Bill> paidBills = (ArrayList<Bill>) billRepository.findByPaidTrue();//findAll();//findByStartDateGreaterThanEqualAndEndDateLessThanEqual(bookingResult.getStartDate(),bookingResult.getEndDate());
		BillList paidBillList = new BillList();
		paidBillList.setBills(paidBills);
		
		ArrayList<Bill> notPaidBills = (ArrayList<Bill>) billRepository.findByPaidFalse();//findAll();//findByStartDateGreaterThanEqualAndEndDateLessThanEqual(bookingResult.getStartDate(),bookingResult.getEndDate());
		BillList notPaidBillList = new BillList();
		notPaidBillList.setBills(notPaidBills);
		
		model.addAttribute("paidBillsWrapper",paidBillList);
		model.addAttribute("notPaidBillsWrapper",notPaidBillList);
		model.addAttribute("bookingResult",newBookingResult);
		model.addAttribute("accountResult", getAccountsResults());
		return "booking";
	}
	
	/**
	 * Calculate the account balance.
	 * @return List of accounts with the current balance
	 */
	private List<AccountResult> getAccountsResults(){
		List<AccountResult> accountResults = new ArrayList<>();
		List<Account> accounts = accountRepository.findAll();
		for(Account account : accounts){
			Float sum = bookingRepository.findCurrentAmountOfAccount(account.getId());
			AccountResult ar = new AccountResult();
			ar.setAccountNumber(account.getAccountNumber());
			if(sum != null)
				ar.setSumOfAmount(sum);
			else
				ar.setSumOfAmount(0f);
			Restaurant restaurant = account.getUsers().get(0).getAdministratedRestaurant();
			
			if(restaurant != null)
				ar.setCustomerId(restaurant.getCustomerId()); 
			
			accountResults.add(ar);
		}
		
		return accountResults;
	}
	
	/**
	 * Search for the admins of a restaurant
	 * @param restaurants List of restaurants
	 * @param id Id of the needed restaurant
	 * @return Admins of the restaurant
	 */
	private List<User> findAdminsOfRestaurant(List<Restaurant> restaurants,int id) {
		
		for(Restaurant r : restaurants){
			if(r.getId() == id){
				return r.getAdmins();
			}
		}
		return null;
	}
	
	/**
	 * Book a deposit in the database.
	 * @param bill needed bill
	 * @param admins admins of the restaurant
	 */
	private void bookingOfDeposit(Bill bill, List<User> admins){
		Date bookingDate = new Date();
		BookingReason bookingReason = bookingReasonRepository.findOne(2); //Einzahlung
		Account ownerAccount = accountRepository.findOne(1);//Besitzerkonto
		Account customerAccount = accountRepository.findByUsers(admins);//findOne(users.getAccount().getId());
		int bookId = getNewBookingId();
		
		//Buchung der Einzahlung auf Forderungskonto des Betreibers
		Booking bookingEntry = new Booking();
		bookingEntry.setBookingTime(bookingDate);
		bookingEntry.setAmount(- bill.getTotalPrice());
		bookingEntry.setBill(bill);
		bookingEntry.setBookingReason(bookingReason);
		bookingEntry.setAccount(ownerAccount);
		bookingEntry.setBookId(bookId);
		
		//Gegenbuchung der Einzahlung auf dem KundenKonto
		Booking contraBookingEntry = new Booking();
		contraBookingEntry.setBookingTime(bookingDate);
		contraBookingEntry.setAmount(bill.getTotalPrice());
		contraBookingEntry.setBill(bill);
		contraBookingEntry.setBookingReason(bookingReason);
		contraBookingEntry.setAccount(customerAccount);
		contraBookingEntry.setBookId(bookId);
		
		bookingRepository.save(bookingEntry);
		bookingRepository.save(contraBookingEntry);
	}
	
	/**
	 * calculate the new BookId.
	 * @return bookId as int
	 */
	private int getNewBookingId(){
		Long c = bookingRepository.count();
		return (c.intValue() / 2) +1;
	}
	
	/**
	 * Calculate sum of claims.
	 * @param bookings List of booking
	 * @return sum of claims
	 */
	private float getSumOfClaim(List<Booking> bookings){
		float result = 0f;
		BookingReason br = bookingReasonRepository.findOne(1);//Forderung
		AccountType at = accountTypeRepository.findByName("Forderungskonto");
		Account ownerAccount = accountRepository.findByAccountTypeId(at.getId());
		for(Booking booking : bookings){
			if(booking.getBookingReason().getReason().equals(br.getReason()) && booking.getAccount().getAccountNumber() == ownerAccount.getAccountNumber())
			result = round(result +  booking.getAmount(),2);
		}
		
		return result;
	}
	
	/**
	 * Get the paid claim from bookings.
	 * @param bookings List of booking
	 * @return sum of paid claims
	 */
	private float getPaidClaim(List<Booking> bookings){
		float result = 0f;
		BookingReason br = bookingReasonRepository.findOne(2); //Einzahlung
		AccountType at = accountTypeRepository.findByName("Forderungskonto");
		Account ownerAccount = accountRepository.findByAccountTypeId(at.getId());
		for(Booking booking : bookings){
			if(booking.getBookingReason().getReason().equals(br.getReason()) && booking.getAccount().getAccountNumber() != ownerAccount.getAccountNumber())
			result = round(result +  booking.getAmount(),2);
		}
		
		return result;
	}
	
	/**
	 * Round a float.
	 * @param value value to round
	 * @param roundDigits needed digits
	 * @return rounded value
	 */
	private float round(float value, int roundDigits){
		BigDecimal bd = new BigDecimal(Float.toString(value));
		bd = bd.setScale(roundDigits, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
	
	/**
	 * Get the start date of the month.
	 * @param monthBefor Month
	 * @return Start date of the month
	 */
	private Date getStartDateOfMonth(LocalDate monthBefor){
		LocalDate start = monthBefor.withDayOfMonth(1);
		Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return startDate;
	}
	/**
	 * Get the last date of the Month.
	 * @param monthBefor month
	 * @return last Date of Month
	 */
	private Date getEndDateOfMonth(LocalDate monthBefor){
		LocalDate lastDay = monthBefor.withDayOfMonth(monthBefor.lengthOfMonth());
		LocalDateTime end = LocalDateTime.of(lastDay.getYear(), lastDay.getMonth(), lastDay.getDayOfMonth(), 23, 59, 59);
		Date endDate = Date.from(end.toInstant(ZoneOffset.of("+01:00"))); //Zoneoffset of Berlin
		return endDate;
	}
}
