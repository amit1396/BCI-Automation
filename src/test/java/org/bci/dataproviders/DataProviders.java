package org.bci.dataproviders;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import org.testng.annotations.DataProvider;

import org.bci.pojoclasses.AdministrationUserPojo;
import org.bci.pojoclasses.LoginPojo;
import org.bci.utilities.ExcelToPojoUtils;
import org.bci.utilities.TestDetails;

public class DataProviders extends BaseDataProvider {

	/**
	 * Generic helper shared by every @DataProvider method below.
	 *
	 * Reads the {@link TestDetails} annotation off the test method, loads the
	 * given Excel sheet as a list of POJOs, and filters to rows where
	 * RunMode = "Y" and TcId matches the test's TestDetails ID.
	 *
	 * @param method        the test method (injected by TestNG), used to read @TestDetails
	 * @param sheetName     the Excel sheet name to load
	 * @param pojoClass     the POJO type each row maps to
	 * @param runModeGetter method reference to the POJO's getRunMode()
	 * @param tcIdGetter    method reference to the POJO's getTcId()
	 */
	private <T> Object[][] getFilteredData(
			Method method, String sheetName, Class<T> pojoClass,
			Function<T, String> runModeGetter, Function<T, String> tcIdGetter) {

		TestDetails testDetails = method.getAnnotation(TestDetails.class);

		if (testDetails == null) {
			throw new RuntimeException("TestDetails annotation missing on method: " + method.getName());
		}

		String tcId = testDetails.id();

		List<T> list = ExcelToPojoUtils.getDataAsPojo(ADMIN_USER_CREATION_EXCEL_PATH, sheetName, pojoClass);

		return list.stream()
				.filter(data -> "Y".equalsIgnoreCase(runModeGetter.apply(data)))
				.filter(data -> tcId.equalsIgnoreCase(tcIdGetter.apply(data)))
				.map(data -> new Object[] { data })
				.toArray(Object[][]::new);
	}

	@DataProvider(name = "UsersData")
	public Object[][] enrollmentInformationDataProvider(Method method) {
		return getFilteredData(
				method, 
				"UsersData", 
				AdministrationUserPojo.class,
				AdministrationUserPojo::getRunMode, 
				AdministrationUserPojo::getTcId
		);
	}
	
	@DataProvider(name = "LoginData")
	public Object[][] loginDataprovider(Method method) {
		return getFilteredData(
				method, 
				"LoginData", 
				LoginPojo.class,
				LoginPojo::getRunMode, 
				LoginPojo::getTcId
				);
	}

	@DataProvider(name = "OPTIVUSLOGINDATA")
	public Object[][] optivusLoginDataprovider(Method method) {
		return getFilteredData(
				method,
				"OPTIVUSLOGINDATA",
				LoginPojo.class,
				LoginPojo::getRunMode,
				LoginPojo::getTcId
		);
	}

}