package io.Rafa_lol.full_Project.query;

public class CustomerQuerry {

    public static final String STATS_QUERY =
            "SELECT c.total_customers, i.total_invoices, inv.total_billed FROM " +
                    "(SELECT COUNT(*) total_customers FROM customer) c, " +
                    "(SELECT COUNT(*) total_invoices FROM invoice) i, " +
                    "(SELECT ROUND(SUM(TOTAL)) total_billed FROM invoice) inv";
}
