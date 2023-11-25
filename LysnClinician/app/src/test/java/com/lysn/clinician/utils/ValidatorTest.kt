package com.lysn.clinician.utils

import org.junit.Assert.*
import org.junit.Test

class ValidatorTest
{
    @Test
    fun test_validateEmailAddress()
    {
        assertTrue(Validator.validateEmailAddress("john@welysn.com"))
    }
}