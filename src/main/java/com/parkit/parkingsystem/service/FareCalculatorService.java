package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

   /*
   methode de calcule calculatefare sans remise
    */
    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, 1);
    }
    /*
    calcule de methode avec remise

     */
    public void calculateFare(Ticket ticket, double pourcentagePayee) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        int inDays = ticket.getInTime().getDay();           // recuperer le jour d 'entrée
        int outDays = ticket.getOutTime().getDay();         // recuperer le jour de sortie
        int inHours = ticket.getInTime().getHours();        // l'heure d 'entrée
        int outHours = ticket.getOutTime().getHours();      // l'heure de sortie
        int inMinutes = ticket.getInTime().getMinutes();    // la minute d'entrée
        int outMinutes = ticket.getOutTime().getMinutes();  // la minute de sortie


        double durationDays = (double) outDays - inDays;
        double durationHours = (double) outHours - inHours;
        double durationMinutes = (double) outMinutes - inMinutes;

        //Si le temps est inférieur à une heure on doit calculer
        //la duréé en minute puis convertir en heure en divisant par 60
        // si la durée dépasse un jour le nombre du jour doit etre *24 puis * 60 convertir le tout en minute puis diviser
        //par 60 pour avoir la duréé en heure vu que le tarif(fare) est calculer en heure

        //’TODO: Some tests are failing here. Need to check if this logic is correct

        double duration = ((durationDays * 24 * 60) + durationHours * 60 + durationMinutes) / 60;


        // si la durée <= 30 min le prix de stationnement est 0
        if (duration <= 0.5) {
            ticket.setPrice(0);

        } else {

            switch (ticket.getParkingSpot().getParkingType()) {

                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * pourcentagePayee);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * pourcentagePayee);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }

        }
    }
}