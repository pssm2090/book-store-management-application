package com.bookstore.reporting.service;

import com.bookstore.dto.auth.UserPurchaseStatsDTO;
import com.bookstore.dto.book.BookInventoryDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

	@Autowired
	private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private BookRepository bookRepository;

    public Map<String, Map<String, BigDecimal>> getAllSalesTrends() {
        Map<String, Map<String, BigDecimal>> result = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // You can adjust the range if needed
        List<Order> orders = orderRepository.findByPlacedAtAfter(startOfMonth);

        Map<String, BigDecimal> dailySales = new TreeMap<>();
        Map<String, BigDecimal> weeklySales = new TreeMap<>();
        Map<String, BigDecimal> monthlySales = new TreeMap<>();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Order order : orders) {
            LocalDate date = order.getPlacedAt().toLocalDate();

            // ---- Daily ----
            String dayKey = date.toString(); // yyyy-MM-dd
            dailySales.put(dayKey, dailySales.getOrDefault(dayKey, BigDecimal.ZERO).add(order.getTotalAmount()));

            // ---- Weekly ----
            int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
            int weekYear = date.get(weekFields.weekBasedYear());
            String weekKey = weekYear + "-W" + String.format("%02d", weekNumber); // e.g., 2025-W33
            weeklySales.put(weekKey, weeklySales.getOrDefault(weekKey, BigDecimal.ZERO).add(order.getTotalAmount()));

            // ---- Monthly ----
            String monthKey = date.getYear() + "-" + String.format("%02d", date.getMonthValue()); // e.g., 2025-08
            monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, BigDecimal.ZERO).add(order.getTotalAmount()));
        }

        result.put("dailySales", dailySales);
        result.put("weeklySales", weeklySales);
        result.put("monthlySales", monthlySales);

        return result;
    }



    public Map<String, Object> getCustomerBehaviorDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        dashboard.put("customerInsights", getCustomerBehaviorReport().get("customerInsights"));
        dashboard.put("mostActiveCustomers", getMostActiveUsers());
        dashboard.put("purchaseTrends", getAllPurchaseTrends());
        dashboard.put("inventoryReport", getInventoryReport());

        return dashboard;
    }

    public Map<String, List<BookInventoryDTO>> getInventoryReport() {
        List<Book> allBooks = bookRepository.findAll();

        List<BookInventoryDTO> lowStock = allBooks.stream()
                .filter(b -> b.getStockQuantity() < 5)
                .map(this::mapToDto)
                .toList();

        List<BookInventoryDTO> outOfStock = allBooks.stream()
                .filter(b -> b.getStockQuantity() == 0)
                .map(this::mapToDto)
                .toList();

        List<Long> topBookIds = orderItemRepository.findTopSellingBookIds(PageRequest.of(0, 5));
        List<Book> bestsellers = bookRepository.findAllById(topBookIds);
        List<BookInventoryDTO> bestsellerDTOs = bestsellers.stream()
                .map(this::mapToDto)
                .toList();

        Map<String, List<BookInventoryDTO>> report = new HashMap<>();
        report.put("lowStock", lowStock);
        report.put("outOfStock", outOfStock);
        report.put("bestsellers", bestsellerDTOs);

        return report;
    }

    
    public Map<String, Object> getCustomerBehaviorReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        List<User> customers = orderRepository.findAllDistinctUsersWithOrders(); // Write this query

        List<Map<String, Object>> customerData = new ArrayList<>();

        for (User customer : customers) {
            List<Order> orders = orderRepository.findByUser(customer);
            if (orders.isEmpty()) continue;

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", customer.getName());
            data.put("email", customer.getEmail());

            long totalOrders = orders.size();
            BigDecimal totalSpent = orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            
            data.put("totalOrders", totalOrders);
            
            data.put("totalSpent", totalSpent);
            
            data.put("averageOrderValue", totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP));

            data.put("purchaseFrequency", calculatePurchaseFrequency(orders));

            data.put("preferredCategories", findTopCategories(orders));

            data.put("monthlySpending", calculateSpendingByMonth(orders));

            data.put("isRepeatCustomer", totalOrders > 1);

            customerData.add(data);
        }

        report.put("customerInsights", customerData);
        return report;
    }
    
    public List<UserPurchaseStatsDTO> getMostActiveUsers() {
        List<Object[]> data = orderRepository.findTopCustomers(); // native/custom query

        return data.stream().map(row -> new UserPurchaseStatsDTO(
                (String) row[0], // email
                (Long) row[1],   // totalOrders
                (BigDecimal) row[2] // totalSpent
        )).toList();
    }
    
    public Map<String, Object> getAllPurchaseTrends() {
        Map<String, Object> trends = new HashMap<>();

        trends.put("monthlyTrends", orderRepository.getMonthlyTrends());
        trends.put("weeklyTrends", orderRepository.getWeeklyTrends());
        trends.put("dailyTrends", orderRepository.getDailyTrends());
        trends.put("bookTrends", orderItemRepository.getBookPurchaseTrends());
        trends.put("userTrends", orderRepository.getUserPurchaseTrends());

        return trends;
    }


    public Map<String, Object> getRevenueAnalytics() {
        Map<String, Object> analytics = new LinkedHashMap<>();

        // Revenue by Category
        List<Object[]> categoryData = orderItemRepository.getRevenueByCategory();
        Map<String, BigDecimal> revenueByCategory = categoryData.stream().collect(Collectors.toMap(
            row -> (String) row[0],
            row -> (BigDecimal) row[1]
        ));
        analytics.put("revenueByCategory", revenueByCategory);

        // Revenue per Book
        List<Object[]> bookData = orderItemRepository.getRevenuePerBook();
        Map<String, BigDecimal> revenuePerBook = new LinkedHashMap<>();
        for (Object[] row : bookData) {
            revenuePerBook.put((String) row[0], (BigDecimal) row[1]);
        }
        analytics.put("revenuePerBook", revenuePerBook);

        // Revenue Over Time — you can run for multiple patterns
        List<String> patterns = List.of("daily", "weekly", "monthly");
        Map<String, Map<String, BigDecimal>> revenueOverTime = new LinkedHashMap<>();
        for (String pattern : patterns) {
            List<Object[]> timeData = orderItemRepository.getRevenueOverTime(pattern);
            Map<String, BigDecimal> timeMap = new LinkedHashMap<>();
            for (Object[] row : timeData) {
                timeMap.put((String) row[0], (BigDecimal) row[1]);
            }
            revenueOverTime.put(pattern, timeMap);
        }
        analytics.put("revenueOverTime", revenueOverTime);

        return analytics;
    }

    
    
    
    
    
    private String calculatePurchaseFrequency(List<Order> orders) {
        if (orders.size() < 2) return "Only 1 order";

        List<LocalDateTime> dates = orders.stream()
                .map(Order::getPlacedAt)
                .sorted()
                .toList();

        List<Long> gaps = new ArrayList<>();
        for (int i = 1; i < dates.size(); i++) {
            long days = Duration.between(dates.get(i - 1), dates.get(i)).toDays();
            gaps.add(days);
        }

        long avgDays = (long) gaps.stream().mapToLong(Long::longValue).average().orElse(0);
        return "Every " + avgDays + " days";
    }

    private List<String> findTopCategories(List<Order> orders) {
        Map<String, Long> categoryCount = new HashMap<>();
        for (Order o : orders) {
            for (OrderItem item : o.getOrderItems()) {
                String cat = item.getBook().getCategory().getName();
                categoryCount.put(cat, categoryCount.getOrDefault(cat, 0L) + 1);
            }
        }
        return categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }

    private Map<String, BigDecimal> calculateSpendingByMonth(List<Order> orders) {
        Map<String, BigDecimal> result = new TreeMap<>();
        for (Order o : orders) {
            String month = o.getPlacedAt().getYear() + "-" + String.format("%02d", o.getPlacedAt().getMonthValue());
            result.put(month, result.getOrDefault(month, BigDecimal.ZERO).add(o.getTotalAmount()));
        }
        return result;
    }

    private BookInventoryDTO mapToDto(Book book) {
        return new BookInventoryDTO(
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            book.getStockQuantity()
        );
    }


}
