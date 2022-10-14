package com.web.common;

import com.web.dto.exception.FormValidateException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class validate {
    //Hàm validate email
    public static String validateEmail(String email){
        boolean checkEmail = regexEmail(email);
        if (checkEmail == true) {
            return email;
        }
        else {
            throw new FormValidateException("email.format", "Email không đúng định dạng");
        }
    }

    //Hàm validate phone
    public static void validatePhone(String phone){
        boolean checkPhone = regexPhone(phone);
        if (checkPhone == true) {
            return;
        }
        else {
            throw new FormValidateException("phone.format","Phone không đúng định dạng");
        }
    }

    //Hàm validate password
    public static void checkPassword(String password){
        if(password != null){
            if (password.length() > 10) {
                throw new FormValidateException("password.format","Mật khẩu không đúng định dạng");
            }
            else {
                return;
            }
        } else
            throw new FormValidateException("phoneNumber", "Số điện thoại không được để trống!");
    }
//
//    // Hàm validate username
//    public static String validateUsername(String username, List<User> users){
//        getSpecialCharacterCount(username);
//        boolean checkUsername = checkUsernameExist(username, users);
//        if (checkUsername == true) {
//            return username;
//        }
//        else {
//            throw new InputException("Username bị trùng");
//        }
//    }
//
//    // Hàm validate serviceName
//    public static String validateServiceName(String serviceName, List<Service> services){
//        getSpecialCharacterCount(serviceName);
//        boolean checkServiceName = checkServiceNameExist(serviceName, services);
//        if (checkServiceName == true) {
//            return serviceName;
//        }
//        else {
//            throw new InputException("dịch vụ đã tồn tại");
//        }
//    }
//
//    // Hàm kiểm tra phone khách hàng đã tồn tại chưa
//    public static String validateCustomerPhone(String phone, List<Customer> customers){
//        getSpecialCharacterCount(phone);
//        boolean checkCustomerPhone = checkCustomerPhoneExist(phone, customers);
//        if (checkCustomerPhone == true) {
//            return phone;
//        }
//        else {
//            throw new InputException("số điện thoại đã tồn tại");
//        }
//    }
//    public static String validateLicensePlate(String licensePlate, List<Vehicle> vehicles){
//        boolean checkLicensePlate = checkLicensePlateExist(licensePlate, vehicles);
//        if (checkLicensePlate == true) {
//            return licensePlate;
//        }
//        else {
//            throw new InputException("Biển số xe đã tồn tại");
//        }
//    }

    //hàm kiểm tra kí tự đặc biệt
    public static boolean getSpecialCharacterCount(String s) {
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        if (b == true)
            return false;
        else
            return true;
    }

    //Hàm kiểm tra input phone
    public static boolean regexPhone(String s) {
        Pattern p = Pattern.compile("^(0[3|5|7|8|9])+([0-9]{8})$");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        if (b == true)
            return true;
        else
            return false;
    }

    //Hàm kiểm tra email
    public static boolean regexEmail(String s) {
        Pattern p = Pattern.compile("^[A-Za-z0-9]+[A-Za-z0-9]*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)$");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        if (b == true)
            return true;
        else
            return false;
    }
//
//    //Hàm kiểm tra tồn tại username
//    public static boolean checkUsernameExist(String username, List<User> users){
//        for (User user:  users) {
//            if (username.compareTo(user.getUsername()) == 0){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    //Hàm kiểm tra tồn tại service name
//    public static boolean checkServiceNameExist(String serviceName, List<Service> services){
//        for (Service service:  services) {
//            if (serviceName.compareTo(service.getName()) == 0){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    //Hàm kiểm tra tồn tại Customer phone
//    public static boolean checkCustomerPhoneExist(String phone, List<Customer> customers){
//        for (Customer customer:  customers) {
//            if (phone.compareTo(customer.getPhone()) == 0){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    //Hàm kiểm tra tồn tại của biển số
//    public static boolean checkLicensePlateExist(String licensePlate,  List<Vehicle> vehicles){
//        for (Vehicle vehicle:  vehicles) {
//            if (licensePlate.compareTo(vehicle.getLicensePlate()) == 0){
//                return false;
//            }
//        }
//        return true;
//    }

}
