# 🏪 OOP Assignment 3: E-Commerce System

## 📌 Overview
This project implements an **e-commerce system** using **object-oriented programming (OOP) principles**. The system supports both customer shopping operations and admin management tasks, functioning entirely through a **command-line interface**.

## 🚀 Key Features
### **Customer Features**
✅ Log in and perform shopping operations  
✅ View order history and consumption reports  

### **Admin Features**
✅ Create, delete, and view customers  
✅ Manage products and orders  
✅ View statistical reports on purchases  

### **System Architecture**
This project follows an **OOP design pattern**, structured as follows:
1. **📦 Model Classes:** Define the data structure and business logic.  
2. **⚙️ Operation Classes:** Handle data access using the **Singleton pattern**.  
3. **🖥️ Main Control Class:** Manages business logic.  
4. **📝 IOInterface Class:** Handles user interaction via `System.out.println()` and `Scanner`.  

## 🛠️ Setup & Installation
### **Prerequisites**
- Install [Java JDK 11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
- Ensure `javac` and `java` commands are accessible from the terminal.

### **Steps to Run**
```sh
git clone https://github.com/yourusername/oop-assignment3.git
cd oop-assignment3
javac -d bin src/*.java
java -cp bin MainControlClass
```

## 🎮 Example Usage
Upon running, the **command-line interface** prompts users to log in:  
```plaintext
+----------------------------------+
|    Welcome to E-Commerce CLI    |
+----------------------------------+
| 1. Customer Login               |
| 2. Admin Login                  |
| 3. Exit                         |
+----------------------------------+
```
Depending on the role:
- Customers can browse products, place orders, and check reports.
- Admins can manage customers, orders, and products.

## ✅ Running Unit Tests
Unit tests are located in the `src/test` directory.

```sh
javac -d bin src/test/*.java
java -cp bin org.junit.runner.JUnitCore TestClassName
```
Replace `TestClassName` with the actual test class name.

## 📂 Project Structure
```plaintext
oop-assignment3/
│── src/               # Source files
│   ├── models/        # Data structure and business logic
│   ├── operations/    # Database handling (Singleton pattern)
│   ├── main/          # Main control logic
│   ├── io/            # Input/Output interactions
│── bin/               # Compiled Java files
│── test/              # Unit test cases
│── README.md          # Documentation
```

## 👥 Contributors
This project was developed as part of **Object-Oriented Programming Assignment 3** by:
- [Nguyen Hung](https://github.com/zodideac)
- [Nguyen Chi Bao](https://github.com/undefined)

## 📝 Examiner Notes
- ✅ Implements **encapsulation, inheritance, and polymorphism**.  
- ✅ Uses **Singleton pattern** for data operations.  
- ✅ Includes **JUnit tests** for functionality verification.  
- ✅ Fully interactive via **command-line interface**.  

---
