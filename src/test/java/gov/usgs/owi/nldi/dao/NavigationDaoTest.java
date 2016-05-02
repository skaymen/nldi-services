package gov.usgs.owi.nldi.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.services.Navigation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@Category(DBIntegrationTest.class)
public class NavigationDaoTest extends BaseSpringTest {

	@Autowired
	NavigationDao navigationDao;

	@Test
	public void navigateTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");
		parameterMap.put(Navigation.STOP_COMID, NumberUtils.parseNumber("13297246", Integer.class));
		parameterMap.put(Navigation.DISTANCE, NumberUtils.parseNumber("10", BigDecimal.class));

		assertNotNull(navigationDao.navigate(parameterMap));
	}

	@Test
	@DatabaseSetup("classpath:/testData/navigationCacheStatus.xml")
	public void getCacheTest() {
		Map<String, Object> parameterMap = new HashMap<>();

		//This one is golden, but we would not get STOP_COMID & DISTANCE on the same query
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");
		parameterMap.put(Navigation.STOP_COMID, NumberUtils.parseNumber("13297246", Integer.class));
		parameterMap.put(Navigation.DISTANCE, NumberUtils.parseNumber("10", BigDecimal.class));

		assertEquals("1a1", navigationDao.getCache(parameterMap));

		//This one is golden
		parameterMap.clear();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");
		parameterMap.put(Navigation.DISTANCE, NumberUtils.parseNumber("10", BigDecimal.class));

		assertEquals("3a3", navigationDao.getCache(parameterMap));

		//This one is golden
		parameterMap.clear();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");
		parameterMap.put(Navigation.STOP_COMID, NumberUtils.parseNumber("13297246", Integer.class));

		assertEquals("4a4", navigationDao.getCache(parameterMap));

		//This one is golden
		parameterMap.clear();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");

		assertEquals("6a6", navigationDao.getCache(parameterMap));

		//This one is golden (note that "10a10" sorts before "8a8")
		parameterMap.clear();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("13297246", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UM");

		assertEquals("10a10", navigationDao.getCache(parameterMap));

		//This one does not exist
		parameterMap.clear();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "PP");
		parameterMap.put(Navigation.STOP_COMID, NumberUtils.parseNumber("13297246", Integer.class));

		assertNull(navigationDao.getCache(parameterMap));

		//This does exist, but has a bad return code
		parameterMap.clear();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("13297246", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "DM");

		assertNull(navigationDao.getCache(parameterMap));

	}

}