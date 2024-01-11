package com.example.assignment.data;



import java.util.HashMap;

public class CrimeData {
    private HashMap<String, HashMap<String, PlaceData>> typeOfCrime;

    public HashMap<String, HashMap<String, PlaceData>> getTypeOfCrime() {
        return typeOfCrime;
    }

    public static class PlaceData {
        private HashMap<String, CrimeDetails> crimeData;

        public HashMap<String, CrimeDetails> getCrimeData() {
            return crimeData;
        }
    }

    public static class CrimeDetails {
        private int murder;
        private int rape;
        private int robberyWithWeapon;
        private int robberyWithoutWeapon;
        private int hurting;
        private int theft;
        private int carTheft;
        private int motorcycleTheft;
        private int lorryVanTheft;
        private int burglary;
        private int total;

        public int getMurder() {
            return murder;
        }

        public int getRape() {
            return rape;
        }

        public int getRobberyWithWeapon() {
            return robberyWithWeapon;
        }

        public int getRobberyWithoutWeapon() {
            return robberyWithoutWeapon;
        }

        public int getHurting() {
            return hurting;
        }

        public int getTheft() {
            return theft;
        }

        public int getCarTheft() {
            return carTheft;
        }

        public int getMotorcycleTheft() {
            return motorcycleTheft;
        }

        public int getLorryVanTheft() {
            return lorryVanTheft;
        }

        public int getBurglary() {
            return burglary;
        }

        public int getTotal() {
            return total;
        }
    }


}
