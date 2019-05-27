package com.example.demo.sales;

import com.example.demo.DemoApplication;
import com.example.demo.common.domain.BusinessPeriod;
import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.domain.POStatus;
import com.example.demo.sales.domain.PurchaseOrder;
import com.example.demo.sales.domain.PurchaseOrderRepository;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class ChangeOfPurchaseOrderSteps {
    @Autowired
    private WebApplicationContext wac;

    private WebClient customerBrowser;
    HtmlPage customerPage;

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantReservationRepository plantReservationRepository;

    @Before  // Use `Before` from Cucumber library
    public void setUp() {
        customerBrowser = MockMvcWebClientBuilder.webAppContextSetup(wac).build();
    }

    @After  // Use `After` from Cucumber library
    public void tearOff() {
        purchaseOrderRepository.deleteAll();
        plantReservationRepository.deleteAll();
        plantInventoryItemRepository.deleteAll();
        plantInventoryEntryRepository.deleteAll();
    }

    @Given("^the following plant catalog$")
    public void the_following_plant_catalog(List<PlantInventoryEntry> entries) throws Throwable {
        plantInventoryEntryRepository.saveAll(entries);
    }

    @Given("^the following inventory$")
    public void the_following_inventory(DataTable table) throws Throwable {
        for (Map<String, String> row: table.asMaps(String.class, String.class))
            plantInventoryItemRepository.save(
                    PlantInventoryItem.of(
                            Long.parseLong(row.get("id")),
                            row.get("serialNumber"),
                            EquipmentCondition.valueOf(row.get("equipmentCondition")),
                            plantInventoryEntryRepository.findById(Long.parseLong(row.get("plantInfo"))).orElse(null)
                    )
            );
    }

    @Given("^the existing PO$")
    public void the_existing_PO(DataTable table) throws Throwable {
        for (Map<String, String> row: table.asMaps(String.class, String.class)) {
            BusinessPeriod period = BusinessPeriod.of(
                    LocalDate.parse(row.get("startDate")),
                    LocalDate.parse(row.get("endDate"))
            );
            PlantInventoryEntry plant = plantInventoryEntryRepository.getOne(Long.parseLong(row.get("id")));
            PurchaseOrder po = PurchaseOrder.of(plant,period);
            po.setStatus(POStatus.valueOf(row.get("status")));
            purchaseOrderRepository.save(po);
            PlantReservation reservation = new PlantReservation();
            reservation.setSchedule(po.getRentalPeriod());
            PlantInventoryItem item = plantInventoryItemRepository.getOne(Long.parseLong(row.get("item_id")));
            reservation.setPlant(item);

            plantReservationRepository.save(reservation);
            po.getReservations().add(reservation);

            purchaseOrderRepository.save(po);
        }
    }

    @When("^the customer wants to change PO period from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void the_customer_wants_to_change_PO_period_from_to(String arg1, String arg2) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

}
