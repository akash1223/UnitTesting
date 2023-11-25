package com.lysn.clinician.viewmodel


import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ConsultationDetailsViewModelTest::class,
    ConsultationListViewModelTest::class,
    JoinConsultationViewModelTest::class,
    SignInViewModelTest::class,
    TermsAndConditionViewModelTest::class,
    VideoSessionViewModelTest::class

)
class ViewModelTestsSuite