# Vo-Card Project

*This project is for educational purposes only.*

Vo-Card is a web application designed to help users practice English using flashcards.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [SQL Setup](#sql-setup)
   - [Automatic Setup](#automatic-setup)
   - [Manual Setup](#manual-setup)
3. [Getting Start](#getting-started)

**For more info on how things operate, please see [developer ducments]()**

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

Here is the simple way on how to start the code :

```bash
# Clone the repository
git clone https://github.com/Vo-Card/Vo-Card.git
cd Vo-Card

# On Windows, you can use the provided dev.bat script:
./dev.bat --all

# On Linux, you can use the provided dev.sh instead:
./dev.sh --all
```
*use `--all` **only once** for initialization. You can run dev without any option afterward (e.g. `./dev.sh`)*

