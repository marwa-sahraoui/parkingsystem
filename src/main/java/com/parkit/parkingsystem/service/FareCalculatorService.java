package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

    /**
     * Methode de calcule calculatefare sans remise
     *
     * @param ticket
     */
    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, 1);
    }

    /**
     * Calcule de methode avec remise
     *
     * @param ticket
     * @param pourcentagePayee
     */
    public void calculateFare(Ticket ticket, double pourcentagePayee) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Duration between = Duration.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant());

        double durationMinutes = (double) between.toMinutes();

        //Si le temps est inférieur à une heure on doit calculer
        //la duréé en minute puis convertir en heure en divisant par 60
        // si la durée dépasse un jour le nombre du jour doit etre *24 puis * 60 convertir le tout en minute puis diviser
        //par 60 pour avoir la duréé en heure vu que le tarif(fare) est calculer en heure

        //’TODO: Some tests are failing here. Need to check if this logic is correct
        Long durationMiliS = between.toMillis();
        double seconds = (double) durationMiliS / 1000 - durationMinutes * 60;
        if (seconds > 30) {
            durationMinutes++;
        }
        double duration = durationMinutes / 60;

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