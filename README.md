# Vo-Card Project

*This project is for educational purposes only.*

Vo-Card is a web application designed to help users practice English using flashcards.

## Prerequisites
Before running the project, ensure you have the following installed:

- **Java 17+**
- **SQL Database** (e.g., [MySQL Workbench](https://www.mysql.com/products/workbench/), [XAMPP](https://www.apachefriends.org/download.html), etc.)
- **[Apache Maven](https://maven.apache.org/download.cgi)**

## SQL Setup

Vo-Card can automatically set up the database for you **if** you use an account with permission to create databases and tables (e.g., `root`).  

### Automatic Setup

- Simply run the application, and it will create the necessary database and tables.

### Manual Setup

- If you prefer to set up the database manually, you can do so.
- **Note:** The current project files do not include a complete SQL structure. For now, it is recommended to use the `root` account to avoid permission issues.

## Getting Started

```bash
# Clone the repository
git clone https://github.com/Vo-Card/Vo-Card.git
cd Vo-Card

# On Windows, you can use the provided dev.bat script:
./dev.bat --All

# On Linux (no bash script yet):
# 1. Build the project
mvn clean install

# 2. Run the project using Tomcat
mvn tomcat9:run
