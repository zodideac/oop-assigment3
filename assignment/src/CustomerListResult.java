import java.util.List;

public class CustomerListResult {
  private List<Customer> customers;
  private int currentPage;
  private int totalPages;

  public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
    this.customers = customers;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
  }

  public List<Customer> getCustomers() { return customers; }
            
  public int getCurrentPage() { return currentPage; }
            
  public int getTotalPages() { return totalPages; }
}