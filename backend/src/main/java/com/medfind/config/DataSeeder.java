package com.medfind.config;

import com.medfind.model.AppUser;
import com.medfind.model.Inventory;
import com.medfind.model.Medicine;
import com.medfind.model.Pharmacy;
import com.medfind.repository.InventoryRepository;
import com.medfind.repository.MedicineRepository;
import com.medfind.repository.PharmacyRepository;
import com.medfind.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    private record SeedMedicine(String name, String brand, String genericGroup, String dosage, boolean prescriptionRequired) {}

    @Bean
    CommandLineRunner seedDatabase(MedicineRepository medicines, PharmacyRepository pharmacies,
                                   InventoryRepository inventory, UserRepository users,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (medicines.count() > 0) return;

            List<Medicine> catalog = new ArrayList<>();
            addCategory(medicines, catalog, "Pain relief", List.of(
                    new SeedMedicine("Paracetamol 500 mg", "Generic", "paracetamol", "500 mg · strip of 10", false),
                    new SeedMedicine("Crocin 500 mg", "Crocin", "paracetamol", "500 mg · strip of 10", false),
                    new SeedMedicine("Paracetamol 650 mg", "Generic", "paracetamol-650", "650 mg · strip of 10", false),
                    new SeedMedicine("Ibuprofen 200 mg", "Generic", "ibuprofen", "200 mg · strip of 10", false),
                    new SeedMedicine("Ibuprofen 400 mg", "IbuRelief", "ibuprofen-400", "400 mg · strip of 10", true),
                    new SeedMedicine("Diclofenac 50 mg", "Voveran", "diclofenac", "50 mg · strip of 10", true),
                    new SeedMedicine("Naproxen 250 mg", "Generic", "naproxen", "250 mg · strip of 10", true),
                    new SeedMedicine("Aspirin 75 mg", "Ecosprin", "aspirin-75", "75 mg · strip of 14", true),
                    new SeedMedicine("Aceclofenac 100 mg", "Generic", "aceclofenac", "100 mg · strip of 10", true),
                    new SeedMedicine("Mefenamic acid 250 mg", "Generic", "mefenamic-acid", "250 mg · strip of 10", true)
            ));
            addCategory(medicines, catalog, "Cold & flu", List.of(
                    new SeedMedicine("Dextromethorphan 10 mg", "Generic", "dextromethorphan", "10 mg · strip of 10", false),
                    new SeedMedicine("Chlorpheniramine 4 mg", "Generic", "chlorpheniramine", "4 mg · strip of 10", false),
                    new SeedMedicine("Guaifenesin 200 mg", "Generic", "guaifenesin", "200 mg · strip of 10", false),
                    new SeedMedicine("Ambroxol 30 mg", "Mucolite", "ambroxol", "30 mg · strip of 10", true),
                    new SeedMedicine("Levocetirizine 5 mg", "Generic", "levocetirizine", "5 mg · strip of 10", false),
                    new SeedMedicine("Phenylephrine 10 mg", "Generic", "phenylephrine", "10 mg · strip of 10", true),
                    new SeedMedicine("Pseudoephedrine 60 mg", "Generic", "pseudoephedrine", "60 mg · strip of 10", true),
                    new SeedMedicine("Zinc acetate lozenges", "Zinco", "zinc-acetate", "10 mg · pack of 10", false),
                    new SeedMedicine("Paracetamol cold relief", "ColdEase", "paracetamol-cold", "500 mg · strip of 10", false),
                    new SeedMedicine("Bromhexine 8 mg", "Generic", "bromhexine", "8 mg · strip of 10", true)
            ));
            addCategory(medicines, catalog, "Vitamins", List.of(
                    new SeedMedicine("Vitamin C 500 mg", "CeePlus", "vitamin-c", "500 mg · bottle of 30", false),
                    new SeedMedicine("Vitamin D3 1000 IU", "D-Sure", "vitamin-d3", "1000 IU · bottle of 30", false),
                    new SeedMedicine("Vitamin B12 500 mcg", "B12Care", "vitamin-b12", "500 mcg · strip of 10", false),
                    new SeedMedicine("Vitamin B-complex", "B-Active", "vitamin-b-complex", "daily tablet · strip of 10", false),
                    new SeedMedicine("Folic acid 5 mg", "Generic", "folic-acid", "5 mg · strip of 10", true),
                    new SeedMedicine("Ferrous sulfate 200 mg", "IronCare", "ferrous-sulfate", "200 mg · strip of 10", true),
                    new SeedMedicine("Calcium carbonate 500 mg", "CalciPlus", "calcium-carbonate", "500 mg · strip of 10", false),
                    new SeedMedicine("Zinc 20 mg", "Zinco", "zinc-20", "20 mg · strip of 10", false),
                    new SeedMedicine("Vitamin E 400 IU", "E-Vital", "vitamin-e", "400 IU · strip of 10", false),
                    new SeedMedicine("Multivitamin tablets", "DailyOne", "multivitamin", "daily tablet · bottle of 30", false)
            ));
            addCategory(medicines, catalog, "Allergy", List.of(
                    new SeedMedicine("Cetirizine 10 mg", "Generic", "cetirizine", "10 mg · strip of 10", false),
                    new SeedMedicine("Cetirizine 10 mg", "Cetzine", "cetirizine", "10 mg · strip of 10", false),
                    new SeedMedicine("Loratadine 10 mg", "Generic", "loratadine", "10 mg · strip of 10", false),
                    new SeedMedicine("Fexofenadine 120 mg", "Allegra", "fexofenadine", "120 mg · strip of 10", true),
                    new SeedMedicine("Levocetirizine 5 mg", "Generic", "levocetirizine", "5 mg · strip of 10", false),
                    new SeedMedicine("Chlorpheniramine 4 mg", "Generic", "chlorpheniramine", "4 mg · strip of 10", false),
                    new SeedMedicine("Montelukast 10 mg", "Montair", "montelukast", "10 mg · strip of 10", true),
                    new SeedMedicine("Desloratadine 5 mg", "Generic", "desloratadine", "5 mg · strip of 10", true),
                    new SeedMedicine("Bilastine 20 mg", "Bilaxten", "bilastine", "20 mg · strip of 10", true),
                    new SeedMedicine("Rupatadine 10 mg", "Generic", "rupatadine", "10 mg · strip of 10", true)
            ));
            addCategory(medicines, catalog, "Digestive", List.of(
                    new SeedMedicine("Omeprazole 20 mg", "Omez", "omeprazole", "20 mg · strip of 10", true),
                    new SeedMedicine("Pantoprazole 40 mg", "Pantocid", "pantoprazole", "40 mg · strip of 10", true),
                    new SeedMedicine("Famotidine 20 mg", "Generic", "famotidine", "20 mg · strip of 10", true),
                    new SeedMedicine("Antacid chewable tablets", "Digene", "antacid", "chewable · strip of 10", false),
                    new SeedMedicine("Simethicone 80 mg", "GasRelief", "simethicone", "80 mg · strip of 10", false),
                    new SeedMedicine("Loperamide 2 mg", "Generic", "loperamide", "2 mg · strip of 10", true),
                    new SeedMedicine("Psyllium husk tablets", "FiberCare", "psyllium", "500 mg · bottle of 30", false),
                    new SeedMedicine("Probiotic capsules", "GutBalance", "probiotic", "daily capsule · strip of 10", false),
                    new SeedMedicine("Domperidone 10 mg", "Generic", "domperidone", "10 mg · strip of 10", true),
                    new SeedMedicine("Oral rehydration salts", "HydraCare", "ors", "21 g · sachet", false)
            ));
            addCategory(medicines, catalog, "Diabetes", List.of(
                    new SeedMedicine("Metformin 500 mg", "Generic", "metformin", "500 mg · strip of 10", true),
                    new SeedMedicine("Metformin 500 mg", "Glycomet", "metformin", "500 mg · strip of 10", true),
                    new SeedMedicine("Glimepiride 1 mg", "Generic", "glimepiride", "1 mg · strip of 10", true),
                    new SeedMedicine("Gliclazide 40 mg", "Generic", "gliclazide", "40 mg · strip of 10", true),
                    new SeedMedicine("Sitagliptin 100 mg", "Januvia", "sitagliptin", "100 mg · strip of 10", true),
                    new SeedMedicine("Empagliflozin 10 mg", "Jardiance", "empagliflozin", "10 mg · strip of 10", true),
                    new SeedMedicine("Dapagliflozin 10 mg", "Forxiga", "dapagliflozin", "10 mg · strip of 10", true),
                    new SeedMedicine("Pioglitazone 15 mg", "Generic", "pioglitazone", "15 mg · strip of 10", true),
                    new SeedMedicine("Vildagliptin 50 mg", "Generic", "vildagliptin", "50 mg · strip of 10", true),
                    new SeedMedicine("Glipizide 5 mg", "Generic", "glipizide", "5 mg · strip of 10", true)
            ));
            addCategory(medicines, catalog, "Skin care", List.of(
                    new SeedMedicine("Clotrimazole 1% cream", "Candid", "clotrimazole", "1% · 15 g tube", false),
                    new SeedMedicine("Terbinafine 250 mg", "Generic", "terbinafine", "250 mg · strip of 7", true),
                    new SeedMedicine("Mupirocin 2% ointment", "T-Bact", "mupirocin", "2% · 5 g tube", true),
                    new SeedMedicine("Calamine lotion", "Caladryl", "calamine", "100 ml · bottle", false),
                    new SeedMedicine("Hydrocortisone 1% cream", "Generic", "hydrocortisone", "1% · 15 g tube", true),
                    new SeedMedicine("Ketoconazole 2% cream", "Nizral", "ketoconazole-cream", "2% · 15 g tube", true),
                    new SeedMedicine("Benzoyl peroxide 2.5% gel", "AcneCare", "benzoyl-peroxide", "2.5% · 20 g tube", false),
                    new SeedMedicine("Adapalene 0.1% gel", "Adaferin", "adapalene", "0.1% · 15 g tube", true),
                    new SeedMedicine("Povidone-iodine 5% ointment", "Generic", "povidone-iodine", "5% · 15 g tube", false),
                    new SeedMedicine("Clindamycin 300 mg", "Generic", "clindamycin", "300 mg · strip of 10", true)
            ));
            addCategory(medicines, catalog, "Antibiotics", List.of(
                    new SeedMedicine("Amoxicillin 500 mg", "Generic", "amoxicillin", "500 mg · strip of 10", true),
                    new SeedMedicine("Amoxicillin 500 mg", "Mox", "amoxicillin", "500 mg · strip of 10", true),
                    new SeedMedicine("Azithromycin 500 mg", "Generic", "azithromycin", "500 mg · strip of 3", true),
                    new SeedMedicine("Cefixime 200 mg", "Generic", "cefixime", "200 mg · strip of 10", true),
                    new SeedMedicine("Cephalexin 500 mg", "Generic", "cephalexin", "500 mg · strip of 10", true),
                    new SeedMedicine("Doxycycline 100 mg", "Generic", "doxycycline", "100 mg · strip of 10", true),
                    new SeedMedicine("Ciprofloxacin 500 mg", "Generic", "ciprofloxacin", "500 mg · strip of 10", true),
                    new SeedMedicine("Clarithromycin 250 mg", "Generic", "clarithromycin", "250 mg · strip of 10", true),
                    new SeedMedicine("Cefuroxime 250 mg", "Generic", "cefuroxime", "250 mg · strip of 10", true),
                    new SeedMedicine("Co-amoxiclav 625 mg", "Generic", "co-amoxiclav", "625 mg · strip of 10", true)
            ));

            List<Pharmacy> stores = pharmacies.saveAll(List.of(
                    new Pharmacy("GreenLeaf Pharmacy", "42, MG Road", "Bengaluru", "+91 80 4567 1200",
                            "Open today · 8:00 AM–10:00 PM", 12.9716, 77.5946),
                    new Pharmacy("CityCare Pharmacy", "18, Residency Road", "Bengaluru", "+91 80 4567 2300",
                            "Open today · 9:00 AM–9:00 PM", 12.9752, 77.6068),
                    new Pharmacy("Neighborhood Meds", "7, Richmond Town", "Bengaluru", "+91 80 4567 3400",
                            "Open today · 7:00 AM–11:00 PM", 12.9584, 77.6011)
            ));

            List<Inventory> offers = new ArrayList<>();
            for (int pharmacyIndex = 0; pharmacyIndex < stores.size(); pharmacyIndex++) {
                for (int medicineIndex = 0; medicineIndex < catalog.size(); medicineIndex++) {
                    BigDecimal price = BigDecimal.valueOf(18 + (medicineIndex * 17 + pharmacyIndex * 11) % 150);
                    int stock = 5 + (medicineIndex * 13 + pharmacyIndex * 17) % 42;
                    offers.add(new Inventory(stores.get(pharmacyIndex), catalog.get(medicineIndex), price, stock));
                }
            }
            inventory.saveAll(offers);

            String encodedPassword = passwordEncoder.encode("Pharmacy123!");
            users.saveAll(List.of(
                    new AppUser("GreenLeaf pharmacist", "admin@greenleaf.com", encodedPassword,
                            "PHARMACIST", stores.get(0).getId()),
                    new AppUser("CityCare pharmacist", "admin@citycare.com", encodedPassword,
                            "PHARMACIST", stores.get(1).getId()),
                    new AppUser("Neighborhood pharmacist", "admin@neighborhood.com", encodedPassword,
                            "PHARMACIST", stores.get(2).getId())
            ));
        };
    }

    private void addCategory(MedicineRepository repository, List<Medicine> catalog, String category,
                             List<SeedMedicine> entries) {
        if (entries.size() < 10) {
            throw new IllegalStateException("Each demo medicine category must contain at least ten items.");
        }
        List<Medicine> saved = repository.saveAll(entries.stream()
                .map(entry -> new Medicine(entry.name(), entry.brand(),
                        category + " catalog listing. Confirm medicine suitability with a pharmacist.",
                        "MED-" + String.format("%03d", catalog.size() + entries.indexOf(entry) + 1),
                        category, entry.genericGroup(), entry.dosage(), entry.prescriptionRequired()))
                .toList());
        catalog.addAll(saved);
    }
}
