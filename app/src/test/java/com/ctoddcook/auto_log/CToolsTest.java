/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.auto_log;

import junit.framework.Assert;

import org.junit.Test;

import static com.ctoddcook.tools.CTools.roundDouble;
import static org.junit.Assert.*;

/**
 * Created by C. Todd Cook on 4/22/2016.
 * ctodd@ctoddcook.com
 */
public class CToolsTest {

    /**
     * Test CTools.roundDouble with the same value, but different scales.
     * @throws Exception
     */
    @Test
    public void testRoundDouble() throws Exception {
        double val = 156.23456;

        Assert.assertEquals(156.2346, roundDouble(val, 4));
        Assert.assertEquals(156.235, roundDouble(val, 3));
        Assert.assertEquals(156.23, roundDouble(val, 2));
        Assert.assertEquals(156.2, roundDouble(val, 1));
        Assert.assertEquals(156.0, roundDouble(val, 0));
        Assert.assertEquals(160.0, roundDouble(val, -1));
    }

}