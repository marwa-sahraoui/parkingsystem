package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        System.out.println("BeforeAll");
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        System.out.println("BeforeEach");
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void calculateWithDiscount() {
        //stationner un vehicule donné
        //mettre un delais >30min modifier à la main la date d'entrée
        //sortir le vehicule
        //checker que le prix de ticket sans remise

        //refais entrer le mm vehicule
        //mettre un delais >30min modifier à la main la date d'entrée
        //sortir le vehicule
        //checker que le prix de ticket sans remise

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();

        Ticket ticketSaved = ticketDAO.getTicket("ABCDEF");
        ticketSaved.setInTime(new Date(ticketSaved.getInTime().getTime() - 60 * 60000));//entréé a une heure avant
        ticketSaved.setOutTime(new Date());
        ticketDAO.updateTicket(ticketSaved); //mise a jour du ticket pour avoir la date de sortie

        parkingService.processExitingVehicle();

        ticketSaved = ticketDAO.getTicket("ABCDEF");
        assertTrue(ticketSaved.getPrice() == Fare.CAR_RATE_PER_HOUR); //ticket pour la premiere fois sans remise

        parkingService.processIncomingVehicle();  //la meme vehicule entre pour une 2 eme fois
        ticketSaved.setInTime(new Date(ticketSaved.getInTime().getTime() - 60 * 60000));
        ticketSaved.setOutTime(new Date());
        parkingService.processExitingVehicle();
        ticketSaved = ticketDAO.getTicket("ABCDEF");// on verifie le prix avec remise
        assertTrue(ticketSaved.getPrice() == Fare.CAR_RATE_PER_HOUR * 0.95);
    }


}

