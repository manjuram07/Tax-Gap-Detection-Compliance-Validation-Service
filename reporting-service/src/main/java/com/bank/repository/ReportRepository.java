package com.bank.repository;

import com.bank.dto.CustomerTaxSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerTaxSummaryResponse getCustomerTaxSummary(UUID customerId) {

        String sql = """
                SELECT
                    customer_id,
                    SUM(amount) AS total_amount,
                    SUM(reported_tax) AS total_reported_tax,
                    SUM(expected_tax) AS total_expected_tax,
                    SUM(tax_gap) AS total_tax_gap,
                    COUNT(*) AS total_transactions,
                    COUNT(*) FILTER (
                        WHERE compliance_status = 'EXPENSE'
                    ) AS non_compliant_transactions
                FROM report_transactions
                WHERE customer_id = :customerId
                GROUP BY customer_id
                """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("customerId", customerId);

        return jdbcTemplate.queryForObject(
                sql,
                parameters,
                (resultSet, rowNumber) -> {

                    long totalTransactions =
                            resultSet.getLong(
                                    "total_transactions"
                            );

                    long nonCompliantTransactions =
                            resultSet.getLong(
                                    "non_compliant_transactions"
                            );

                    BigDecimal complianceScore =
                            calculateComplianceScore(
                                    totalTransactions,
                                    nonCompliantTransactions
                            );

                    return new CustomerTaxSummaryResponse(
                            resultSet.getObject(
                                    "customer_id",
                                    UUID.class
                            ),

                            resultSet.getBigDecimal(
                                    "total_amount"
                            ),

                            resultSet.getBigDecimal(
                                    "total_reported_tax"
                            ),

                            resultSet.getBigDecimal(
                                    "total_expected_tax"
                            ),

                            resultSet.getBigDecimal(
                                    "total_tax_gap"
                            ),

                            totalTransactions,

                            nonCompliantTransactions,

                            complianceScore
                    );
                }
        );
    }

    private BigDecimal calculateComplianceScore(
            long totalTransactions,
            long nonCompliantTransactions) {

        if (totalTransactions == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal nonCompliantPercentage =
                BigDecimal.valueOf(
                                nonCompliantTransactions
                        )
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(
                                        totalTransactions
                                ),
                                2,
                                RoundingMode.HALF_UP
                        );

        return BigDecimal.valueOf(100)
                .subtract(nonCompliantPercentage);
    }
}



