package com.ai.services;

import com.ai.data.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.function.Function;

@Slf4j
@Configuration
public class BookingTools {

    @Autowired
	private FlightBookingService flightBookingService;

    @JsonInclude(Include.NON_NULL)
    public record BookingDetails(String bookingNumber, String name, LocalDate date, BookingStatus bookingStatus,
                                 String from, String to, String bookingClass) {
    }

    public record BookingRequest(String bookingNumber,
                                 String name) {
    }

    @Bean
	@Description("处理机票退订")
    public Function<BookingRequest, String> cancelBooking() {
		return c -> {
			flightBookingService.cancelBooking(c.bookingNumber(), c.name());
			return "退订成功";
		};
	}

    @Bean
    @Description("修改机票预订日期")
    public Function<BookingDetails, String> changeBooking() {
        return c -> {
            flightBookingService.changeBooking(c.bookingNumber(), c.name(), c.date().toString(), c.from(), c.to());
            return "修改成功";
        };
    }

    @Bean
    @Description("获取指定机票详情")
    public Function<BookingRequest, BookingDetails> bookingDetail() {
        return c -> {
            try {
                return flightBookingService.getBookingDetails(c.bookingNumber(), c.name());
            } catch (Exception e) {
                log.warn("Booking details could not be retrieved. Number: {}, Name: {}", c.bookingNumber(), c.name());
                return new BookingDetails(c.bookingNumber(), c.name(), null, null, null, null, null);
            }
        };
    }

}
