package com.lysn.clinician

import com.lysn.clinician.model.ModelTestsSuite
import com.lysn.clinician.repository.RepoTestsSuite
import com.lysn.clinician.utils.UtilsTestsSuite
import com.lysn.clinician.viewmodel.*
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    ViewModelTestsSuite::class,
    UtilsTestsSuite::class,
    RepoTestsSuite::class,
    ModelTestsSuite::class

)
class AllTestsSuite {
}