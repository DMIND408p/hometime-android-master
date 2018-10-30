package au.com.realestate.hometime;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mainActivityActivityTestRule.getActivity();
    }
    @Test

    public void testLaunch(){
        View view = mActivity.findViewById(R.id.northListView);
        assertNotNull(view);


    }
    @Test
    public void onCreate() {
    }

    @Test
    public void refreshClick() {
    }

    @Test
    public void clearClick() {
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

    }


}