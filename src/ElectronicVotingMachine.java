import java.io.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

class User {
    protected String userID;
    protected String password;

    public User(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }
}

class Admin extends User {
    public Admin(String adminID, String adminPassword) {
        super(adminID, adminPassword);
    }
}

class Voter extends User {
    private boolean hasVoted;

    public Voter(String voterID, String voterPassword) {
        super(voterID, voterPassword);
        this.hasVoted = false;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setVoted(boolean voted) {
        this.hasVoted = voted;
    }
}

public class ElectronicVotingMachine {
    private static final String CANDIDATES_FILE_PATH = "candidates.txt";
    private static final String VOTERS_FILE_PATH = "voters.txt";
    private static final String VOTES_FILE_PATH = "votes.txt";

    private Map<String, Integer> candidates;
    private Map<String, Boolean> voters;

    public ElectronicVotingMachine() {
        candidates = new HashMap<>();
        voters = new HashMap<>();
        loadCandidates();
        loadVoters();
    }

    private void loadCandidates() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                candidates.put(line.trim(), 0);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Candidates file not found. Creating a new one.");
        } catch (IOException e) {
            System.out.println("Error loading candidates: " + e.getMessage());
        }
    }

    private void loadVoters() {
        try (BufferedReader reader = new BufferedReader(new FileReader(VOTERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    voters.put(parts[0].trim(), Boolean.parseBoolean(parts[1].trim()));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Voters file not found. Creating a new one.");
        } catch (IOException e) {
            System.out.println("Error loading voters: " + e.getMessage());
        }
    }

    private void saveVotes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VOTES_FILE_PATH))) {
            for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving votes: " + e.getMessage());
        }
    }

    private void saveCandidates() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CANDIDATES_FILE_PATH))) {
            for (String candidate : candidates.keySet()) {
                writer.write(candidate + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving candidates: " + e.getMessage());
        }
    }

    private void saveVoters() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VOTERS_FILE_PATH))) {
            for (Map.Entry<String, Boolean> entry : voters.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving voters: " + e.getMessage());
        }
    }

    public void addCandidate(String name) {
        candidates.put(name, 0);
        saveCandidates();
    }

    public void removeCandidate(String name) {
        if (candidates.containsKey(name)) {
            candidates.remove(name);
            saveCandidates();
            System.out.println("Candidate removed: " + name);
        } else {
            System.out.println("Candidate not found.");
        }
    }

    public void addVoter(String voterID) {
        voters.put(voterID, false);
        saveVoters();
    }

    public void removeVoter(String voterID) {
        if (voters.containsKey(voterID)) {
            voters.remove(voterID);
            saveVoters();
            System.out.println("Voter removed: " + voterID);
        } else {
            System.out.println("Voter not found.");
        }
    }

    public boolean authenticateAdmin(Admin admin) {
        return admin.userID.equals("admin") && admin.password.equals("12345");
    }

    public boolean authenticateVoter(Voter voter) {
        return voters.containsKey(voter.userID) && !voter.hasVoted();
    }

    public void vote(Voter voter, String candidateName) {
        if (authenticateVoter(voter)) {
            if (candidates.containsKey(candidateName)) {
                int currentVotes = candidates.get(candidateName);
                candidates.put(candidateName, currentVotes + 1);
                voter.setVoted(true);
                System.out.println("Vote for " + candidateName + " recorded.");
                saveVotes();
                saveVoters();
            } else {
                System.out.println("Invalid candidate name.");
            }
        } else {
            System.out.println("Invalid voter ID or you have already voted.");
        }
    }

    public void displayResults() {
        System.out.println("Election Results:");
        for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
            String candidateName = entry.getKey();
            int candidateVotes = entry.getValue();
            System.out.println(candidateName + ": " + candidateVotes + " votes");
        }
    }

    public void displayCandidates() {
        System.out.println("List of Candidates:");
        for (String candidate : candidates.keySet()) {
            System.out.println(candidate);
        }
    }

    public void displayVoters() {
        System.out.println("List of Voters:");
        for (Map.Entry<String, Boolean> entry : voters.entrySet()) {
            String voterID = entry.getKey();
            boolean hasVoted = entry.getValue();
            System.out.println(voterID + ": " + (hasVoted ? "Voted" : "Not Voted"));
        }
    }

    public static void main(String[] args) {
        ElectronicVotingMachine evm = new ElectronicVotingMachine();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n=== Electronic Voting Machine Menu ===");
                System.out.println("1. Admin Login");
                System.out.println("2. Voter Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice (1-3): ");

                int userType = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (userType) {
                    case 1:
                        System.out.print("Enter Admin ID: ");
                        String adminID = scanner.nextLine();
                        System.out.print("Enter Admin Password: ");
                        String adminPassword = scanner.nextLine();

                        Admin admin = new Admin(adminID, adminPassword);
                        if (evm.authenticateAdmin(admin)) {
                            while (true) {
                                System.out.println("\n=== Admin Menu ===");
                                System.out.println("1. Add Candidate");
                                System.out.println("2. Remove Candidate");
                                System.out.println("3. Add Voter");
                                System.out.println("4. Remove Voter");
                                System.out.println("5. Display Candidates");
                                System.out.println("6. Display Results");
                                System.out.println("7. Display Voters");
                                System.out.println("8. Logout");
                                System.out.print("Enter your choice (1-8): ");

                                int adminChoice = scanner.nextInt();
                                scanner.nextLine(); // Consume newline

                                switch (adminChoice) {
                                    case 1:
                                        System.out.print("Enter candidate name: ");
                                        String addCandidateName = scanner.nextLine();
                                        evm.addCandidate(addCandidateName);
                                        System.out.println("Candidate added: " + addCandidateName);
                                        break;

                                    case 2:
                                        System.out.print("Enter candidate name to remove: ");
                                        String removeCandidateName = scanner.nextLine();
                                        evm.removeCandidate(removeCandidateName);
                                        break;

                                    case 3:
                                        System.out.print("Enter voter ID: ");
                                        String addVoterID = scanner.nextLine();
                                        evm.addVoter(addVoterID);
                                        System.out.println("Voter added: " + addVoterID);
                                        break;

                                    case 4:
                                        System.out.print("Enter voter ID to remove: ");
                                        String removeVoterID = scanner.nextLine();
                                        evm.removeVoter(removeVoterID);
                                        break;

                                    case 5:
                                        evm.displayCandidates();
                                        break;

                                    case 6:
                                        evm.displayResults();
                                        break;

                                    case 7:
                                        evm.displayVoters();
                                        break;

                                    case 8:
                                        System.out.println("Logging out as Admin.");
                                        break;

                                    default:
                                        System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                                        break;
                                }

                                if (adminChoice == 8) {
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Invalid Admin ID or Password.");
                        }
                        break;

                    case 2:
                        System.out.print("Enter your voter ID: ");
                        String voteVoterID = scanner.nextLine();

                        Voter voter = new Voter(voteVoterID, ""); // You may add voter password input
                        if (evm.authenticateVoter(voter)) {
                            System.out.println("Candidates:");
                            evm.displayCandidates();  // Display all candidates to the voter
                            System.out.print("Enter the name of your preferred candidate: ");
                            String vote = scanner.nextLine();
                            evm.vote(voter, vote);
                        } else {
                            System.out.println("Invalid voter ID or you have already voted.");
                        }
                        break;

                    case 3:
                        System.out.println("Exiting the program.");
                        scanner.close();
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the buffer
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // Clear the buffer
            }
        }
    }
}
