package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Account;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Bill;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Booking;
import edu.hm.cs.projektstudium.findlunch.webapp.model.BookingReason;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DonationPerMonth;
import edu.hm.cs.projektstudium.findlunch.webapp.model.MinimumProfit;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationOffers;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BillRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BookingReasonRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.BookingRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DonationPerMonthRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.MinimumProfitRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;

/**
 * The Class BillScheduledTask.
 * */
@Component
public class BillScheduledTask {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(BillScheduledTask.class);
	
	/** The Restaurant repo.*/
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/** The Reservation repo.*/
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The DonationPerMonth repo.*/
	@Autowired
	private DonationPerMonthRepository donationPerMonthRepository;
	
	/** The MinimumProfit repo.*/
	@Autowired
	private MinimumProfitRepository minimumProfitRepository;
	
	/** The Bill repo.*/
	@Autowired
	private BillRepository billRepository;
	
	/** The Booking repo.*/
	@Autowired
	private BookingRepository bookingRepository;
	
	/** The BookingReason repo.*/
	@Autowired
	private BookingReasonRepository bookingReasonRepository;
	
	/** The Account repo.*/
	@Autowired
	private AccountRepository accountRepository;

	private final static String FL_COMPANY = "FindLunch GmbH";
	private final static String FL_OWNER = "Max Mustermann";
	private final static String FL_STREET = "Lothstraße";
	private final static String FL_STREETNUMBER = "64";
	private final static String FL_CITY = "München";
	private final static String FL_ZIP = "80355";
	private final static String FL_IBAN = "DE12500105170648489890";
	private final static String FL_BIC = "HYVEDEMMXXX";
	private final static String FL_USTID = "USt-IdNr.: DE123456789";
	
	private final static String DOT = " \u2022 ";
	private final static String nL = System.getProperty("line.separator");
	private final static String DATE_PATTERN = "dd.MM.yyyy";
	private final static String BILLS_PATH = "bills\\"; //= "src\\main\\resources\\bills\\";
	private final static String FL_IMG = "static/images/FL.png"; //= "src\\main\\resources\\static\\images\\FL.png";
	
	/**
	 * Create the bills.
	 * */
//	@Scheduled(cron="0 0 0 1 * ?")   //call one time per month  //sec min hour day month weekday  *=all ?=not specific
	@Scheduled(initialDelay=2000,fixedRate = 180000)// call every 3 minute
	public void createBill(){
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Starting to create bills."));
		LocalDate monthBefor = LocalDate.now().minusDays(1);
		syncDonations(monthBefor);
		ResourceBundle messages = getResurceBundel();
		
		File dir = new File(BILLS_PATH);
		if(!dir.exists()){
			dir.mkdir();
		}
		 
		StringBuilder FlHeader = new StringBuilder();
		FlHeader.append(FL_COMPANY).append(DOT).append(FL_OWNER).append(DOT).append(FL_STREET).append(" ").append(FL_STREETNUMBER).append(DOT).append(FL_ZIP).append(" ").append(FL_CITY);
		String headerInfo = FlHeader.toString();
		String bankInformation = FL_IBAN + DOT + FL_BIC;
		
		Image image = getImage();
		
		//configure Header
		Phrase restInfo = new Phrase(nL + nL+ headerInfo + nL + nL);
		image.scalePercent(65);
		Chunk cImage = new Chunk(image, 400, -45);
		restInfo.add(cImage);
		HeaderFooter header = new HeaderFooter(restInfo, false);
		header.setBorder(Rectangle.NO_BORDER);
		
		//configure Footer
		Paragraph footParagraph = new Paragraph(headerInfo + nL + bankInformation + nL + FL_USTID);
		HeaderFooter footer = new HeaderFooter(footParagraph,true);
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(Rectangle.NO_BORDER);
		
		//needed Dates
		Date endDate = getEndDateOfMonth(monthBefor);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		String todayString = sdf.format(new Date());
		String dateWithCity = FL_CITY +", "+ todayString;
		String endDateString = sdf.format(endDate);
		Font boldFont = new Font(Font.DEFAULTSIZE, 12, Font.BOLD);
		

		
		List<Restaurant> restaurants = restaurantRepository.findAll();
		for(Restaurant restaurant : restaurants) {
			List<Reservation> reservations = reservationRepository.findByRestaurantIdAndReservationStatusKeyAndBillIdOrderByTimestampReceivedAsc(restaurant.getId(), ReservationStatus.RESERVATION_KEY_CONFIRMED, null);
			
			if(reservations.size() > 0 ) {
				List<DonationPerMonth> donationsOfSupplier = donationPerMonthRepository.findByRestaurantIdAndBillIdOrderByDateDesc(restaurant.getId(), null);
				float restaurantDonationBrutto = 0;
				for(DonationPerMonth donationPerMonth : donationsOfSupplier){
					restaurantDonationBrutto =  round(restaurantDonationBrutto + donationPerMonth.getAmount(), 2);
				}
				
				float sum = restaurantDonationBrutto;
				for(Reservation reservation : reservations){
					sum = round(sum + reservation.getDonation(), 2);
				}
				int billCounter = java.lang.Math.toIntExact(billRepository.count()+1);
				MinimumProfit minimumProfit = minimumProfitRepository.findOne(1);
				if(sum >= minimumProfit.getProfit()){
					
					Date startDate = null;
					LocalDate start;
					//exist for every month is always the oldest date
					if(!donationsOfSupplier.isEmpty()){
						DonationPerMonth oldestSupplierDonation = donationsOfSupplier.get(donationsOfSupplier.size()-1);
						start = new java.sql.Date(oldestSupplierDonation.getDate().getTime()).toLocalDate();
					}
					//added for demonstration the bill creating after the first bill in the current month 
					//(usually this is not needed, because one bill will be created per month)
					//to create only one bill per month, change the comment form the @Scheduled in the header of this method
					else{
						Reservation oldestReservation = reservations.get(0);
						start = oldestReservation.getTimestampReceived().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					}

					startDate = getStartDateOfMonth(start);
					String startDateString = sdf.format(startDate);
					
					Document doc = new Document();
					String billNumber = getBillNumber(monthBefor, billCounter);
					String path = BILLS_PATH + restaurant.getName()+"_"+billNumber+".pdf";
					float findLunchEarning = 0;
					try {
						
						PdfWriter.getInstance(doc, new FileOutputStream(path));

						doc.setHeader(header);
						doc.setFooter(footer);
						doc.open();
						
						StringBuilder restaurantAddress = new StringBuilder();
						restaurantAddress.append(restaurant.getName()).append(nL).append(restaurant.getEmail()).append(nL)
							.append(restaurant.getStreet()).append(" ").append(restaurant.getStreetNumber()).append(nL).append(restaurant.getZip()).append(" ").append(restaurant.getCity());
						
						doc.add(new Paragraph(restaurantAddress.toString()));
						
						Paragraph custNrParagraph = new Paragraph(MessageFormat.format(messages.getString("bill.customerNumber"), Integer.toString(restaurant.getCustomerId())));
						custNrParagraph.setAlignment(Element.ALIGN_RIGHT);
						doc.add(custNrParagraph);
						
						Paragraph dateParagraph = new Paragraph(dateWithCity);
						dateParagraph.setAlignment(Element.ALIGN_RIGHT);
						doc.add(dateParagraph);
						
						String billspace = MessageFormat.format(messages.getString("bill.space"), billNumber, startDateString, endDateString);
						doc.add(new Paragraph(billspace+nL+nL, boldFont));
						doc.add(new Paragraph(messages.getString("bill.introduction") + nL +nL));
						
				        PdfPTable table = new PdfPTable(6);
				        table.setWidthPercentage(100);
				        int[] columnWidths = new int[]{4, 8, 12, 20,  8, 15};
				        table.getDefaultCell().setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
				        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			
						Font font = new Font(Font.DEFAULTSIZE, 11, Font.BOLD);
				        table.addCell(new Phrase(messages.getString("bill.pos"), font));
				        table.addCell(new Phrase(messages.getString("bill.bestNr"), font));
				        //table.addCell(new Phrase(messages.getString("bill.product"), font));
				        table.addCell(new Phrase(messages.getString("bill.date"), font));
				        table.addCell(new Phrase(messages.getString("bill.customerString"), font));
				        //table.addCell(new Phrase(messages.getString("bill.unit"), font));
				        //table.addCell(new Phrase(messages.getString("bill.price"), font));
				        table.addCell(new Phrase(messages.getString("bill.totalPrice"), font));
				        table.addCell(new Phrase(messages.getString("bill.customerDonation"), font));
				        table.setHeaderRows(1);
				        
						float restaurantEarning = 0f;
						float customDonationBrutto = 0f;
						
						for (int i = 0; i < reservations.size(); i++){
							Reservation reservation = reservations.get(i);
							restaurantEarning = round(restaurantEarning + reservation.getTotalPrice(),2);
							customDonationBrutto = round(customDonationBrutto + reservation.getDonation(),2);
							
							font = new Font(Font.DEFAULTSIZE, 11, Font.NORMAL);
							table.addCell(new Phrase(Integer.toString(i), font));
							table.addCell(new Phrase(Integer.toString(reservation.getReservationNumber()), font));
							table.addCell(new Phrase(sdf.format(reservation.getTimestampReceived()), font));
							table.addCell(new Phrase(reservation.getUser().getUsername(), font));
							table.addCell(new Phrase(floatToString(reservation.getTotalPrice()), font));
							table.addCell(new Phrase(floatToString(reservation.getDonation()), font));
						}
						
				        table.setWidths(columnWidths);
				        doc.add(table);
				        doc.newPage();
				        
						float sumOfEarnings = round(restaurantEarning + customDonationBrutto,2);						
						float customDonationNetto = round(customDonationBrutto / 1.19f, 2);
						float customTax = round(customDonationNetto*0.19f, 2);
						float restaurantDonationNetto = round(restaurantDonationBrutto / 1.19f, 2);
						float restaurantTax = round(restaurantDonationNetto*0.19f, 2);
						
						String earnings = MessageFormat.format(messages.getString("bill.earnings"), sumOfEarnings, (sumOfEarnings - customDonationBrutto) );
						doc.add(new Paragraph(nL+ earnings +nL +nL));
						doc.add(createTable(messages.getString("bill.netto"), customDonationNetto, messages.getString("bill.tax"), customTax, messages.getString("bill.brutto"), customDonationBrutto));
						
						if(restaurantDonationBrutto > 0){
							String thanks = MessageFormat.format(messages.getString("bill.thanks"), restaurantDonationBrutto);
							doc.add(new Paragraph(nL+thanks +nL + nL));
							doc.add(createTable(messages.getString("bill.netto"), restaurantDonationNetto, messages.getString("bill.tax"), restaurantTax, messages.getString("bill.brutto"), restaurantDonationBrutto));
						}
						else{
							doc.add(new Paragraph(nL+messages.getString("bill.noSupportForRestaurant")));
						}
						
						doc.add(new Paragraph(nL));
						PdfPTable tableRemittance = new PdfPTable(1);
						findLunchEarning = round((restaurantDonationBrutto+customDonationBrutto), 2);
						String remittanceInfo = MessageFormat.format(messages.getString("bill.remittance"), findLunchEarning, FL_IBAN, FL_BIC, startDateString, endDateString);
						tableRemittance.addCell(remittanceInfo);
						tableRemittance.setWidthPercentage(100);
						
						doc.add(tableRemittance);
						doc.add(new Paragraph(nL+nL+messages.getString("bill.goodbye")));
			
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (DocumentException e) {
						e.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
					
					doc.close();
		
					Bill bill = new Bill();
					bill.setStartDate(startDate);
					bill.setEndDate(endDate);
					bill.setRestaurant(restaurant);
					bill.setPaid(false);
					bill.setBillNumber(billNumber);
					bill.setTotalPrice(findLunchEarning);
					bill.setListOfDonationPerMonth(donationsOfSupplier);
					bill.setMinimumProfit(minimumProfit);
					bill.setReservations(reservations);

					
					
					File file = new File(path);
					byte[] pdfByte = null;
					try {
						pdfByte = Files.readAllBytes(file.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					bill.setBillPdf(pdfByte);
					billRepository.save(bill);
					
					for(Reservation r : reservations){
						r.setBill(bill);
						reservationRepository.save(r);
					}
					
					for(DonationPerMonth d : donationsOfSupplier){
						d.setBill(bill);
						donationPerMonthRepository.save(d);
					}
					
					billCounter++;
					bookingOfClaim(bill, restaurant.getAdmins());
					file.delete();
					
					LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"For the Restaurant "+restaurant.getName() +" was create a bill. The Restaurant have " + sum + ". This is  over defined minimum Profit:"+minimumProfit.getProfit()));
				}
				else{
					LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Restaurant "+restaurant.getName() +" have " + sum + ". This is not enough to create a bill. A bill will be create >= "+minimumProfit.getProfit()));
				}
			}
			else{
				LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Restaurant "+restaurant.getName() +" have no Reservation" ));
			}
		}
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Finished with creating the bills."));
	}
	
	/**
	 * Book a claim in the database.
	 * @param bill needed bill
	 * @param admins admins of the Restaurant
	 */
	private void bookingOfClaim(Bill bill, List<User> admins){
		Date bookingDate = new Date();
		BookingReason bookingReason = bookingReasonRepository.findOne(1); //Forderung
		Account ownerAccount = accountRepository.findOne(1);//Besitzerkonto
		Account customerAccount = accountRepository.findByUsers(admins);
		int bookId = getNewBookingId();
		
		//Buchung der Forderung auf Forderungskonto des Betreibers
		Booking bookingEntry = new Booking();
		bookingEntry.setBookingTime(bookingDate);
		bookingEntry.setAmount(bill.getTotalPrice());
		bookingEntry.setBill(bill);
		bookingEntry.setBookingReason(bookingReason);
		bookingEntry.setAccount(ownerAccount);
		bookingEntry.setBookId(bookId);
		
		//Gegenbuchung der Forderung auf dem KundenKonto
		Booking contraBookingEntry = new Booking();
		contraBookingEntry.setBookingTime(bookingDate);
		contraBookingEntry.setAmount(- bill.getTotalPrice());
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
	 * returns the FindLunch Image.
	 * @return Image
	 */
	private Image getImage(){
		InputStream is = getClass().getClassLoader().getResourceAsStream(FL_IMG);
		BufferedImage img = null;
		try {
			img = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] imageInByte = null;
		try {
			ImageIO.write(img, "png", baos );
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		Image image = null;
		try {
			image = Image.getInstance(imageInByte); //getPath() + FL_IMG
		} catch (BadElementException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return image;
	}
	
	/**
	 * calculate the new Bill Number.
	 * @param monthBefor monthBefor
	 * @param billCounter current bill Counter
	 * @return billNumber as a String
	 */
	private String getBillNumber(LocalDate monthBefor, int billCounter){
		int year = monthBefor.getYear();
		int month = monthBefor.getMonthValue();
		StringBuilder billNumber = new StringBuilder();
		billNumber.append(year).append("-").append(month).append("-").append(billCounter);
		return billNumber.toString();
	}
	
	/**
	 * calculate the start date of the month.
	 * @param monthBefor monthBefor
	 * @return the start date of the last month
	 */
	private Date getStartDateOfMonth(LocalDate monthBefor){
		LocalDate start = monthBefor.withDayOfMonth(1);
		Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return startDate;
	}
	
	/**
	 * calculate the end date of the month.
	 * @param monthBefor monthBefor
	 * @return the end date of the last month
	 */
	private Date getEndDateOfMonth(LocalDate monthBefor){
		LocalDate lastDay = monthBefor.withDayOfMonth(monthBefor.lengthOfMonth());
		LocalDateTime end = LocalDateTime.of(lastDay.getYear(), lastDay.getMonth(), lastDay.getDayOfMonth(), 23, 59, 59);
		Date endDate = Date.from(end.toInstant(ZoneOffset.of("+01:00"))); //Zoneoffset of Berlin
		return endDate;
	}
	
	/**
	 * Create a Table for the pdf document.
	 * @param netto Name of netto 
	 * @param customDonationNetto Customer netto donation
	 * @param tax Name of Tax
	 * @param customTax paid tax
	 * @param brutto Name of brutto
	 * @param customDonationBrutto Customer brutto donation
	 * @return table with the needed information for the pdf document
	 */
	private PdfPTable createTable(String netto, float customDonationNetto, String tax, float customTax, String brutto, float customDonationBrutto){
		PdfPTable tableDonation = new PdfPTable(2);
		tableDonation.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		tableDonation.setWidthPercentage(50);
		tableDonation.addCell(netto);
		tableDonation.addCell(floatToString(customDonationNetto));
		tableDonation.addCell(tax);
		tableDonation.addCell(floatToString(customTax));
		tableDonation.getDefaultCell().setBorder(PdfPCell.TOP);
		tableDonation.addCell(brutto);
		tableDonation.addCell(floatToString(customDonationBrutto));
		tableDonation.setHorizontalAlignment(Element.ALIGN_LEFT);
		return tableDonation;
	}
	
	/**
	 * Convert a Float to String.
	 * @param f float to convert
	 * @return float as a String
	 */
	private String floatToString(float f){
		StringBuilder sfloat = new StringBuilder(Float.toString(f).replace(".", ","));
		sfloat.append(" €");
		return sfloat.toString();
	}
	
	/**
	 * rounds a value.
	 * @param value value
	 * @param roundDigits digits to round
	 * @return rounded value
	 */
	private float round(float value, int roundDigits){
		BigDecimal bd = new BigDecimal(Float.toString(value));
		bd = bd.setScale(roundDigits, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
	
	/**
	 * Checks the donation from a restaurant and create a new donation for it when it is necessary.
	 * @param monthBefor month to check
	 */
	private void  syncDonations(LocalDate monthBefor){
		List<Restaurant> restaurants = restaurantRepository.findAll();
		for(Restaurant restaurant : restaurants){
			DonationPerMonth currentDonationPerMonth = donationPerMonthRepository.findFirstByRestaurantIdOrderByDateDesc(restaurant.getId());
			Date startDateOflastMonth = getStartDateOfMonth(monthBefor);
			DonationPerMonth newDonation = new DonationPerMonth();
			if(currentDonationPerMonth != null) {
				int result = currentDonationPerMonth.getDate().compareTo(startDateOflastMonth);
				
				if(result < 0){//-1 is befor startDateOflastMonth, create donation for this last month
					newDonation.setAmount(currentDonationPerMonth.getAmount());
					newDonation.setDate(startDateOflastMonth);
					newDonation.setDatetimeOfUpdate(new Date());
					newDonation.setRestaurant(restaurant);
					restaurant.addDonationPerMonth(newDonation);
					donationPerMonthRepository.save(newDonation);
					
				}//for equal and after non update needed, because they already exist in database
			}
			else{
				//never befor spend a donation, (default donation is 0)
				newDonation.setAmount(0f);
				newDonation.setDate(startDateOflastMonth);
				newDonation.setDatetimeOfUpdate(new Date());
				newDonation.setRestaurant(restaurant);
				restaurant.addDonationPerMonth(newDonation);
				donationPerMonthRepository.save(newDonation);
			}
		}
	}
	
	/**
	 * ResourceBundle to get the content for the bill. 
	 * @return ResourceBundle
	 */
	private ResourceBundle getResurceBundel(){
		Locale currentLocale;
        currentLocale = new Locale("de", "DE");
        ResourceBundle messages = ResourceBundle.getBundle("messages.bill", currentLocale);
		return messages;
	}
}