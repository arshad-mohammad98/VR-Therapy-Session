package com.vrtherapy.service;

import com.vrtherapy.dao.TicketDao;
import com.vrtherapy.dto.SupportTicketDto;
import com.vrtherapy.dto.TicketReplyDto;
import com.vrtherapy.model.*;
import com.vrtherapy.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private TicketDao ticketDao;

    @InjectMocks
    private SupportTicketServiceImpl ticketService;

    private Patient patient;
    private Admin admin;

    @BeforeEach
    void setUp() {
        patient = new Patient("patient1", "pass", "Sarah", "sarah@example.com", true);
        patient.setId(10L);

        admin = new Admin("admin", "pass", "Admin User", "admin@example.com", true);
        admin.setId(1L);
    }

    @Test
    @DisplayName("Should create support ticket with OPEN status")
    void testCreateTicket() {
        SupportTicketDto dto = new SupportTicketDto();
        dto.setTitle("Headset Calibration Error");
        dto.setDescription("Controllers are drifting in WebXR mode.");
        dto.setPriority(TicketPriority.HIGH);

        when(ticketDao.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket ticket = ticketService.createTicket(dto, patient);

        assertNotNull(ticket);
        assertEquals("Headset Calibration Error", ticket.getTitle());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals(TicketPriority.HIGH, ticket.getPriority());
    }

    @Test
    @DisplayName("Admin replying to an OPEN ticket should transition status to IN_PROGRESS")
    void testAdminReplyTransitionsTicketStatus() {
        SupportTicket ticket = new SupportTicket(10L, "Issue", "Problem description", TicketPriority.MEDIUM);
        ticket.setId(42L);
        ticket.setStatus(TicketStatus.OPEN);

        when(ticketDao.findById(42L)).thenReturn(Optional.of(ticket));

        TicketReplyDto replyDto = new TicketReplyDto(42L, "Investigating this issue now.");
        ticketService.addReply(replyDto, admin);

        verify(ticketDao).addReply(any(TicketReply.class));
        verify(ticketDao).updateStatus(42L, TicketStatus.IN_PROGRESS);
    }
}
