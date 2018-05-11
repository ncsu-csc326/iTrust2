package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfiguration.class, WebMvcConfiguration.class })
@WebAppConfiguration

public class APIICDCodeTest {
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    public void testCodeAPI() throws Exception {
        final ICDCodeForm form = new ICDCodeForm();
        form.setCode("T12");
        form.setDescription("Test Code");

        String content = mvc.perform(
                post("/api/v1/icdcodes").contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(form)))
                .andReturn().getResponse().getContentAsString();
        final Gson gson = new GsonBuilder().create();
        ICDCodeForm code = gson.fromJson(content, ICDCodeForm.class);
        form.setId(code.getId()); // fill in the id of the code we just created
        assertEquals(form, code);

        content = mvc.perform(get("/api/v1/icdcode/" + form.getId()).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        code = gson.fromJson(content, ICDCodeForm.class);
        assertEquals(form, code);

        // edit it
        form.setCode("T13");
        content = mvc.perform(put("/api/v1/icdcode/" + form.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(form))).andReturn().getResponse().getContentAsString();
        code = gson.fromJson(content, ICDCodeForm.class);
        assertEquals(form, code);
        content = mvc.perform(get("/api/v1/icdcode/" + form.getId()).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        code = gson.fromJson(content, ICDCodeForm.class);
        assertEquals(form, code);

        // then delete it and check that its gone.
        mvc.perform(delete("/api/v1/icdcode/" + form.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertNull(ICDCode.getById(form.getId()));
        mvc.perform(get("/api/v1/icdcode/" + form.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
