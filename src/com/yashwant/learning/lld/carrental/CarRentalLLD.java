package com.yashwant.learning.lld.carrental;

import java.time.LocalDate;
import java.util.*;
import java.util.List;

//create LLD in java using OOPs for car rental system.
//below features:
//1. user can search based on city and duration.
//2. user should be seeing only available cars in the city.
//3. no two users can book same car for same duration.

public class CarRentalLLD {
 public static void main(String[] args) {
  CarRentalSystem system = new CarRentalSystem();

  // Add some cars to the system
  system.addCar(new Car("C1", "Toyota Camry", "New York"));
  system.addCar(new Car("C2", "Honda Accord", "New York"));
  system.addCar(new Car("C3", "Tesla Model 3", "San Francisco"));

  // Create a user
  User user = new User("U1", "John Doe");

  // Search cars
  findBooking(system, LocalDate.now(), LocalDate.now().plusDays(1), user);
  findBooking(system, LocalDate.now(), LocalDate.now().plusDays(1), user);
  findBooking(system, LocalDate.now(), LocalDate.now().plusDays(1), user);
  findBooking(system, LocalDate.now().minusDays(3), LocalDate.now().minusDays(2), user);

 }

 private static void findBooking(CarRentalSystem system, LocalDate from, LocalDate to, User user) {
  List<Car> availableCars = system.searchCars("New York", from, to);
  System.out.println("Available cars in New York:");
  for (Car car : availableCars) {
   System.out.println(car.getModel());
  }

  // Book a car
  if (!availableCars.isEmpty()) {
   boolean bookingSuccess = system.bookCar(user, availableCars.get(0), from, to);
   if (bookingSuccess) {
    System.out.println("Car booked successfully!");
   }
  }
  else{
   System.out.println("No car found!");
  }

  // View bookings
  System.out.println("All bookings:");
  for (Booking booking : system.getAllBookings()) {
   System.out.println("Booking ID: " + booking.getBookingId() + ", Car: " + booking.getCar().getModel());
  }
 }
}

class CarRentalSystem {
 private Map<String, List<Car>> carsByCity;
 private Map<String, TreeMap<LocalDate, LocalDate>> bookingsByCar;

 public CarRentalSystem() {
  this.carsByCity = new HashMap<>();
  this.bookingsByCar = new TreeMap<>();
 }

 // Add a car to the system
 public void addCar(Car car) {
  carsByCity.putIfAbsent(car.getCity(), new ArrayList<>());
  carsByCity.get(car.getCity()).add(car);

  bookingsByCar.putIfAbsent(car.getCarId(), new TreeMap<>());
 }

 // Search for available cars based on city and duration
 public List<Car> searchCars(String city, LocalDate startDate, LocalDate endDate) {
  List<Car> carsInCity = carsByCity.getOrDefault(city, Collections.emptyList());
  List<Car> availableCars = new ArrayList<>();

  for (Car car : carsInCity) {
   if (isCarAvailable(car, startDate, endDate)) {
    availableCars.add(car);
   }
  }
  return availableCars;
 }

 // Check if a car is available for the given duration
 private boolean isCarAvailable(Car car, LocalDate startDate, LocalDate endDate) {
  TreeMap<LocalDate, LocalDate> carBookings = bookingsByCar.get(car.getCarId());
  if (carBookings == null || carBookings.isEmpty()) {
   return true;
  }

  // Find the nearest booking that starts before or at the start date
  Map.Entry<LocalDate, LocalDate> previousBooking = carBookings.floorEntry(startDate);

  // Find the nearest booking that starts after or at the end date
  Map.Entry<LocalDate, LocalDate> nextBooking = carBookings.ceilingEntry(startDate);

  // Check overlap with the previous booking
  if (previousBooking != null && !previousBooking.getValue().isBefore(startDate)) {
   return false;
  }

  // Check overlap with the next booking
  if (nextBooking != null && !endDate.isBefore(nextBooking.getKey())) {
   return false;
  }

  return true;
 }

 // Book a car
 public boolean bookCar(User user, Car car, LocalDate startDate, LocalDate endDate) {
  if (isCarAvailable(car, startDate, endDate)) {
   TreeMap<LocalDate, LocalDate> carBookings = bookingsByCar.get(car.getCarId());
   carBookings.put(startDate, endDate);

   Booking booking = new Booking(
           "B" + UUID.randomUUID(), user, car, startDate, endDate);
   System.out.println("Booking successful for car: " + car.getModel() + " for duration: from " + startDate + " to " + endDate);
   return true;
  } else {
   System.out.println("Car is not available for the selected duration.");
   return false;
  }
 }

 // View all bookings for debugging or reporting
 public List<Booking> getAllBookings() {
  List<Booking> allBookings = new ArrayList<>();
  for (Map.Entry<String, TreeMap<LocalDate, LocalDate>> entry : bookingsByCar.entrySet()) {
   for (Map.Entry<LocalDate, LocalDate> bookingEntry : entry.getValue().entrySet()) {
    allBookings.add(new Booking(
            "B", null, new Car(entry.getKey(), "", ""), bookingEntry.getKey(), bookingEntry.getValue()));
   }
  }
  return allBookings;
 }
}



 class Booking {
  private String bookingId;
  private User user;
  private Car car;
  private LocalDate startDate;
  private LocalDate endDate;

  public Booking(String bookingId, User user, Car car, LocalDate startDate, LocalDate endDate) {
   this.bookingId = bookingId;
   this.user = user;
   this.car = car;
   this.startDate = startDate;
   this.endDate = endDate;
  }

  public String getBookingId() {
   return bookingId;
  }

  public User getUser() {
   return user;
  }

  public Car getCar() {
   return car;
  }

  public LocalDate getStartDate() {
   return startDate;
  }

  public LocalDate getEndDate() {
   return endDate;
  }
 }

class Car {
 private String carId;
 private String model;
 private String city;
 private boolean isAvailable;

 public Car(String carId, String model, String city) {
  this.carId = carId;
  this.model = model;
  this.city = city;
  this.isAvailable = true;
 }

 public String getCarId() {
  return carId;
 }

 public String getModel() {
  return model;
 }

 public String getCity() {
  return city;
 }

 public boolean isAvailable() {
  return isAvailable;
 }

 public void setAvailable(boolean available) {
  isAvailable = available;
 }
}

class User {
 private String userId;
 private String name;

 public User(String userId, String name) {
  this.userId = userId;
  this.name = name;
 }

 public String getUserId() {
  return userId;
 }

 public String getName() {
  return name;
 }
}

