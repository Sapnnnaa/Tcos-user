package com.telco.telco_co_app.service;

import com.telco.telco_co_app.model.CDShareDeductions;
import com.telco.telco_co_app.model.Loan;
import com.telco.telco_co_app.model.LoanType;
import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.repository.LoanRespository;
import com.telco.telco_co_app.model.Company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AnnualStatementPdfService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private LoanRespository loanRepository;

    @Autowired
    private CDShareDeductionService cdShareDeductionService;

    @Autowired
    private CompanyService companyService;


    public byte[] generateAnnualStatementPdf(User currentUser) {

        // =========================================
        // CD SHARE DATA
        // =========================================

        List<CDShareDeductions> cdShareList =
                cdShareDeductionService.findByUserId(
                        currentUser.getUserId()
                );

        // =========================================
// LATEST CD SHARE RECORD
// =========================================

        CDShareDeductions latestCdShare = null;

        if (cdShareList != null && !cdShareList.isEmpty()) {

            latestCdShare = cdShareList.stream()
                    .filter(data -> data.getPaymentDate() != null)
                    .max((a, b) ->
                            a.getPaymentDate()
                                    .compareTo(b.getPaymentDate()))
                    .orElse(null);
        }


// =========================================
// SHARE / CD VALUES FOR 2025 STATEMENT
// =========================================

        double shareCapital = 0.0;
        double compulsoryDeposit = 0.0;
        double cdInterest = 0.0;

        if (latestCdShare != null) {

            shareCapital =
                    latestCdShare.getShareDeductionTotal();

            compulsoryDeposit =
                    latestCdShare.getCdDeductionTotal();

            cdInterest =
                    latestCdShare.getCdInterestTotal();
        }

        String companyName = "";

        Company company = companyService.findbyCompanyId(
                currentUser.getCompanyId()
        );

        if (company != null) {
            companyName = company.getCompanyName();
        }


        // =========================================
        // PERSONAL LOAN
        // =========================================

        Loan personalLoan =
                loanRepository
                        .findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                                currentUser.getUserId(),
                                LoanType.PERSONAL
                        );


        // =========================================
        // COMMODITY LOAN
        // =========================================

        Loan commodityLoan =
                loanRepository
                        .findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                                currentUser.getUserId(),
                                LoanType.COMMODITY
                        );


        // =========================================
        // LOAN BALANCES
        // =========================================

        double personalLoanBalance = 0;

        double commodityLoanBalance = 0;


        if (personalLoan != null) {

            personalLoanBalance =
                    personalLoan.getLoanBalance();
        }


        if (commodityLoan != null) {

            commodityLoanBalance =
                    commodityLoan.getLoanBalance();
        }


        double totalLoanBalance =
                personalLoanBalance
                        + commodityLoanBalance;


        // =========================================
        // PAYMENT YEAR
        // =========================================

        String paymentYear = "";


        if (cdShareList != null
                && !cdShareList.isEmpty()) {

            /*
             * If findByUserId() returns records in
             * database order, use the latest record.
             *
             * Better would be to order the repository
             * query by paymentDate/createdAt DESC.
             */

            CDShareDeductions latest =
                    cdShareList.get(cdShareList.size() - 1);


            if (latest.getPaymentDate() != null) {

                Calendar calendar =
                        Calendar.getInstance();

                calendar.setTime(
                        latest.getPaymentDate()
                );

                paymentYear =
                        String.valueOf(
                                calendar.get(Calendar.YEAR)
                        );
            }
        }


        // =========================================
        // LATEST LOAN DATE
        // =========================================

        Date latestLoanDate = null;


        if (personalLoan != null
                && personalLoan.getLoanApproveDate() != null) {

            latestLoanDate =
                    personalLoan.getLoanApproveDate();
        }


        if (commodityLoan != null
                && commodityLoan.getLoanApproveDate() != null) {

            if (latestLoanDate == null
                    || commodityLoan
                    .getLoanApproveDate()
                    .after(latestLoanDate)) {

                latestLoanDate =
                        commodityLoan.getLoanApproveDate();
            }
        }


        // =========================================
        // LOAN PAYMENT YEAR
        // =========================================

        String loanPaymentYear = "";


        if (latestLoanDate != null) {

            Calendar calendar =
                    Calendar.getInstance();

            calendar.setTime(
                    latestLoanDate
            );

            loanPaymentYear =
                    String.valueOf(
                            calendar.get(Calendar.YEAR)
                    );
        }


        // =========================================
        // STATEMENT YEAR
        // =========================================

        String statementYear =
                !loanPaymentYear.isEmpty()
                        ? loanPaymentYear
                        : paymentYear;


        // =========================================
// THYMELEAF CONTEXT
// =========================================

        Context context =
                new Context();

        context.setVariable(
                "memberName",
                currentUser.getName()
        );

        context.setVariable(
                "personalNumber",
                currentUser.getUserId()
        );

        context.setVariable(
                "company",
                companyName
        );


// =========================================
// SHARE / CD VALUES
// =========================================

        context.setVariable(
                "shareCapital",
                String.format(
                        "%,.2f",
                        shareCapital
                )
        );

        context.setVariable(
                "compulsoryDeposit",
                String.format(
                        "%,.2f",
                        compulsoryDeposit
                )
        );

        context.setVariable(
                "cdInterest",
                String.format(
                        "%,.2f",
                        cdInterest
                )
        );


// =========================================
// OTHER DATA
// =========================================

        context.setVariable(
                "cdshare",
                cdShareList
        );

        context.setVariable(
                "paymentYear",
                paymentYear
        );

        context.setVariable(
                "loanPaymentYear",
                loanPaymentYear
        );

        context.setVariable(
                "personalLoanBalance",
                String.format(
                        "%,.2f",
                        personalLoanBalance
                )
        );

        context.setVariable(
                "commodityLoanBalance",
                String.format(
                        "%,.2f",
                        commodityLoanBalance
                )
        );


        context.setVariable(
                "totalLoanBalance",
                String.format(
                        "%,.2f",
                        totalLoanBalance
                )
        );


        // =========================================
        // GENERATE PDF
        // =========================================

        return pdfService.generatePdf(
                "pdf/annual_statement",
                context
        );
    }
}