package com.example.hrms.biz.booking.service;

import com.example.hrms.biz.booking.model.Booking;
import com.example.hrms.biz.booking.model.criteria.BookingCriteria;
import com.example.hrms.biz.booking.model.dto.BookingDTO;
import com.example.hrms.biz.booking.repository.BookingMapper;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.*;
import java.util.*;

import com.example.hrms.enumation.BookingType;

@Service
public class BookingService {
    private final BookingMapper bookingMapper;

    public BookingService(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    public Booking getBookingById(Long bookingId) {
        return bookingMapper.selectById(bookingId);
    }

    public void insert(BookingDTO.Req req) {
        Booking booking = req.toBooking();
        handleBookingType(booking);
        bookingMapper.insert(booking);
    }

    public void updateBooking(Booking booking) {
        handleBookingType(booking);
        bookingMapper.updateBooking(booking);
    }

    public void deleteBooking(Long bookingId) {
        bookingMapper.deleteBooking(bookingId);
    }

    public boolean isConflict(Booking booking) {
        List<Booking> conflictingBookings = bookingMapper.findConflictingBookings(
                booking.getRoomId(), booking.getStartTime(), booking.getEndTime()
        );
        return !conflictingBookings.isEmpty();
    }

    public int count(BookingCriteria criteria) {
        return bookingMapper.count(criteria);
    }

    public List<BookingDTO.Resp> list(BookingCriteria criteria) {
        List<Booking> bookings = bookingMapper.select(criteria);
        return bookings.stream().map(BookingDTO.Resp::toResponse).toList();
    }

    public List<BookingDTO.Resp> getAllBookings() {
        List<Booking> bookings = bookingMapper.selectAll();
        return bookings.stream().map(BookingDTO.Resp::toResponse).toList();
    }

    // Xử lý booking theo loại
    public List<Booking> handleBookingType(Booking booking) {
        List<Booking> generatedBookings = new ArrayList<>();

        if (booking.getBookingType() == null) {
            booking.setBookingType(BookingType.ONLY);
        }

        LocalTime startTime = booking.getStartTime().toLocalTime();
        LocalTime endTime = booking.getEndTime().toLocalTime();

        switch (booking.getBookingType()) {
            case ONLY:
                // Mặc định ngày hôm nay + thời gian
                LocalDate today = LocalDate.now();
                booking.setStartTime(LocalDateTime.of(today, startTime));
                booking.setEndTime(LocalDateTime.of(today, endTime));
                booking.setWeekdays(null);
                generatedBookings.add(booking);
                break;

            case DAILY:
                // Tạo booking mỗi ngày từ start đến end
                LocalDate dailyStart = booking.getStartTime().toLocalDate();
                LocalDate dailyEnd = booking.getEndTime().toLocalDate();

                for (LocalDate date = dailyStart; !date.isAfter(dailyEnd); date = date.plusDays(1)) {
                    Booking b = copyBooking(booking);
                    b.setStartTime(LocalDateTime.of(date, startTime));
                    b.setEndTime(LocalDateTime.of(date, endTime));
                    b.setBookingType(BookingType.ONLY);
                    b.setWeekdays(null);
                    generatedBookings.add(b);
                }
                break;

            case WEEKLY:
                // Phải có weekdays hợp lệ
                if (booking.getWeekdays() == null || booking.getWeekdays().isBlank()) {
                    throw new IllegalArgumentException("WEEKLY booking requires weekdays.");
                }

                Set<DayOfWeek> selectedDays = parseWeekdays(booking.getWeekdays());
                LocalDate weeklyStart = booking.getStartTime().toLocalDate();
                LocalDate weeklyEnd = booking.getEndTime().toLocalDate();

                for (LocalDate date = weeklyStart; !date.isAfter(weeklyEnd); date = date.plusDays(1)) {
                    if (selectedDays.contains(date.getDayOfWeek())) {
                        Booking b = copyBooking(booking);
                        b.setStartTime(LocalDateTime.of(date, startTime));
                        b.setEndTime(LocalDateTime.of(date, endTime));
                        b.setBookingType(BookingType.ONLY);
                        b.setWeekdays(null);
                        generatedBookings.add(b);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid booking type");
        }

        return generatedBookings;
    }

    // Hàm phụ: Tạo bản sao booking
    private Booking copyBooking(Booking original) {
        Booking b = new Booking();
        b.setUsername(original.getUsername());
        b.setRoomId(original.getRoomId());
        b.setTitle(original.getTitle());
        b.setAttendees(original.getAttendees());
        b.setContent(original.getContent());
        b.setStatus(original.getStatus());
        return b;
    }

    // Hàm phụ: Chuyển chuỗi weekdays thành Set<DayOfWeek>
    private Set<DayOfWeek> parseWeekdays(String weekdays) {
        Map<String, DayOfWeek> map = Map.of(
                "Mo", DayOfWeek.MONDAY,
                "Tu", DayOfWeek.TUESDAY,
                "We", DayOfWeek.WEDNESDAY,
                "Th", DayOfWeek.THURSDAY,
                "Fr", DayOfWeek.FRIDAY,
                "Sa", DayOfWeek.SATURDAY,
                "Su", DayOfWeek.SUNDAY
        );

        return Arrays.stream(weekdays.split(","))
                .map(String::trim)
                .map(map::get)
                .collect(Collectors.toSet());
    }
}
