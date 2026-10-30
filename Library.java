import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;

class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private boolean isBorrowed;   

    Book() {
        id = 0;
        title = "unknown";
        category = "unknown";
        author = "unknown";
        isBorrowed = false;
    }

    Book(int id, String title, String category, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isBorrowed = false; 
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getAuthor() { return author; }
    public boolean isBorrowed() { return isBorrowed; }
    public void setBorrowed(boolean borrowed) { isBorrowed = borrowed; }

    public void borrowBook() {
        if (!isBorrowed) {
            setBorrowed(true);
            System.out.println(title + " has been borrowed.");
        } else {
            System.out.println(title + " is currently borrowed.");
        }
    }

    public void returnBook() {
        if (isBorrowed) {
            setBorrowed(false);
            System.out.println(title + " has been returned.");
        } else {
            System.out.println(title + " is already available.");
        }
    }

    // Turning object into string
    public String toFileString() {
        return id + "," + title + "," + category + "," + author + "," + isBorrowed;
    }

    // Book object from file
    public static Book fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            try {
                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String category = parts[2].trim();
                String author = parts[3].trim();
                boolean isBorrowed = Boolean.parseBoolean(parts[4].trim());
                
                Book b = new Book(id, title, category, author);
                b.setBorrowed(isBorrowed);
                return b;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void getDetails() {
        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Category: " + category);
        System.out.println("Author: " + author);
        if (isBorrowed) {
            System.out.println("Status: Borrowed");
        } else {
            System.out.println("Status: Available");
        }
        System.out.println("--------------------------");
    }
}

class GUIClass extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusBar;
    private ArrayList<Book> books;
    private final String FILE_NAME = "Book.txt";

    public GUIClass() {
        this.books = new ArrayList<>();

        // Frame settings
        setTitle("Library System - Pink Theme");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Colors theme
        Color lightPink = new Color(255, 228, 235);
        Color mediumPink = new Color(255, 182, 193);
        Color deepPink = new Color(219, 112, 147);
        Color darkText = new Color(80, 40, 50);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(deepPink);
        JLabel headerLabel = new JLabel("Welcome to the Library System");
        headerLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Book table
        String[] columnNames = {"ID", "Title", "Category", "Author", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setSelectionBackground(mediumPink);
        table.setSelectionForeground(darkText);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        // Status bar
        statusBar = new JLabel(" Status: Initializing...");
        statusBar.setFont(new Font("Times New Roman", Font.ITALIC, 12));
        statusBar.setForeground(darkText);
        statusBar.setBorder(BorderFactory.createLineBorder(mediumPink));
        add(statusBar, BorderLayout.SOUTH);

        // Loading data
        loadBooksFromFile();
        refreshTableData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(lightPink);
        add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(5, 1, 10, 10));
        leftPanel.setBackground(lightPink);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnBorrow = createStyledButton("1. Borrow Book", deepPink);
        JButton btnReturn = createStyledButton("2. Return Book", deepPink);
        JButton btnSearch = createStyledButton("3. Search Catalog", deepPink);
        JButton btnDetails = createStyledButton("4. View Details", deepPink);
        JButton btnQuit = createStyledButton("5. Quit", deepPink);

        leftPanel.add(btnBorrow);
        leftPanel.add(btnReturn);
        leftPanel.add(btnSearch);
        leftPanel.add(btnDetails);
        leftPanel.add(btnQuit);

        add(leftPanel, BorderLayout.WEST);

        // Buttons logic
        btnBorrow.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Book b = findBookById(id);
                if (b != null) {
                    if (!b.isBorrowed()) {
                        b.borrowBook();
                        saveBooksToFile();
                        refreshTableData();
                        JOptionPane.showMessageDialog(this, b.getTitle() + " has been successfully borrowed!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Book is already borrowed!");
                        statusBar.setText(" Status: Ready.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book from the table first.");
                statusBar.setText(" Status: Ready.");
            }
        });

        btnReturn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Book b = findBookById(id);
                if (b != null) {
                    if (b.isBorrowed()) {
                        b.returnBook();
                        saveBooksToFile();
                        refreshTableData();
                        JOptionPane.showMessageDialog(this, b.getTitle() + " has been returned successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Book is already available!");
                        statusBar.setText(" Status: Ready.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book from the table first.");
                statusBar.setText(" Status: Ready.");
            }
        });

        btnSearch.addActionListener(e -> {
            statusBar.setText(" Status: Reading catalog file for search query...");
            String query = JOptionPane.showInputDialog(this, "Enter Book Title or ID to Search:");
            if (query != null && !query.trim().isEmpty()) {
                boolean found = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String title = tableModel.getValueAt(i, 1).toString();
                    String idStr = tableModel.getValueAt(i, 0).toString();
                    if (title.equalsIgnoreCase(query.trim()) || idStr.equals(query.trim())) {
                        table.setRowSelectionInterval(i, i);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    statusBar.setText(" Status: Read complete. Book found in catalog.");
                    JOptionPane.showMessageDialog(this, "Book found and highlighted in the table!");
                } else {
                    statusBar.setText(" Status: Read complete. Book not found.");
                    JOptionPane.showMessageDialog(this, "Book not found in catalog.");
                }
            } else {
                statusBar.setText(" Status: Ready.");
            }
        });

        btnDetails.addActionListener(e -> {
            statusBar.setText(" Status: Reading book details from memory...");
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Book b = findBookById(id);
                if (b != null) {
                    String info = "ID: " + b.getId() + "\nTitle: " + b.getTitle() + 
                                  "\nCategory: " + b.getCategory() + "\nAuthor: " + b.getAuthor() + 
                                  "\nStatus: " + (b.isBorrowed() ? "Borrowed" : "Available");
                    JOptionPane.showMessageDialog(this, info, "Book Details", JOptionPane.INFORMATION_MESSAGE);
                    statusBar.setText(" Status: Ready.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book from the table to view details.");
                statusBar.setText(" Status: Ready.");
            }
        });

        btnQuit.addActionListener(e -> {
            statusBar.setText(" Status: Saving and closing application...");
            System.exit(0);
        });
    }

    private void loadBooksFromFile() {
        statusBar.setText(" Status: Loading files... Reading from " + FILE_NAME);
        File file = new File(FILE_NAME);

        // Reader
        books.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Book b = Book.fromFileString(line);
                if (b != null) {
                    books.add(b);
                }
            }
            statusBar.setText(" Status: Files loaded successfully | Read operation complete.");
        } catch (IOException e) {
            statusBar.setText(" Status: Error reading file.");
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void saveBooksToFile() {
        statusBar.setText(" Status: Writing data... Updating " + FILE_NAME);
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Book b : books) {
                writer.println(b.toFileString());
            }
            statusBar.setText(" Status: Write complete. Changes saved to " + FILE_NAME);
        } catch (IOException e) {
            statusBar.setText(" Status: Error writing file.");
            JOptionPane.showMessageDialog(this, "Error saving to file: " + e.getMessage());
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        return button;
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        for (Book b : books) {
            Object[] row = {b.getId(), b.getTitle(), b.getCategory(), b.getAuthor(), b.isBorrowed() ? "Borrowed" : "Available"};
            tableModel.addRow(row);
        }
    }

    private Book findBookById(int id) {
        for (Book b : books) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        // GUI dispatch
        SwingUtilities.invokeLater(() -> {
            GUIClass gui = new GUIClass();
            gui.setVisible(true);
        });
    }
}