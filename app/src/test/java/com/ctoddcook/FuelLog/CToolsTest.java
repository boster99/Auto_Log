/*
 * Copyright (c) 2016 C. Todd Cook. All rights reserved.
 */

package com.ctoddcook.FuelLog;

import junit.framework.Assert;

import org.junit.Test;

import static com.ctoddcook.CamGenTools.CTools.round;

/**
 * Created by C. Todd Cook on 4/22/2016.
 * ctodd@ctoddcook.com
 */
public class CToolsTest {

    /**
     * Test CTools.round with the same value, but different scales.
     * @throws Exception
     */
    @Test
    public void testRoundDouble() throws Exception {
        double val = 156.23456;

        Assert.assertEquals(156.2346, round(val, 4));
        Assert.assertEquals(156.235, round(val, 3));
        Assert.assertEquals(156.23, round(val, 2));
        Assert.assertEquals(156.2, round(val, 1));
        Assert.assertEquals(156.0, round(val, 0));
        Assert.assertEquals(160.0, round(val, -1));
    }

}