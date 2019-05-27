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
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class ChangeOfPurchaseOrderSteps {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
            PlantInventoryItem item = plantInventoryItemRepository.findOneByPlantInfo(plant);
            reservation.setPlant(item);

            plantReservationRepository.save(reservation);
            po.getReservations().add(reservation);

            purchaseOrderRepository.save(po);
        }
    }

    @When("^the customer wants to change PO period from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void the_customer_wants_to_change_PO_period_from_to(String arg1, String arg2) throws Throwable {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut request = new HttpPut("http://localhost:8080/api/sales/orders/1/changePeriod");
        JSONObject json = new JSONObject();
        json.put("startDate", arg1);
        json.put("endDate", arg2);
        StringEntity se = new StringEntity( "JSON: " + json.toString());
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        request.setEntity(se);
        httpClient.execute(request);
    }

    @Then("^PO period is changed from \\\"([^\\\"]*)\\\" to \\\"([^\\\"]*)\\\"$\"$")
    public void po_period_is_changed(String arg1, String arg2) throws Throwable {
        PurchaseOrder po = purchaseOrderRepository.getOne(Long.parseLong("1"));
        BusinessPeriod expectedPeriod = BusinessPeriod.of(
                LocalDate.parse(arg1),
                LocalDate.parse(arg2)
        );

        assert(po.getRentalPeriod()).equals(expectedPeriod);
    }
}
