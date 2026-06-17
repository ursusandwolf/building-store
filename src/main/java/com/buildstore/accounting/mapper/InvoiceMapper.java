package com.buildstore.accounting.mapper;

import com.buildstore.accounting.dto.InvoiceLineResponse;
import com.buildstore.accounting.dto.InvoiceResponse;
import com.buildstore.accounting.model.Invoice;
import com.buildstore.accounting.model.InvoiceLine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceResponse toResponse(Invoice invoice);
    InvoiceLineResponse toLineResponse(InvoiceLine invoiceLine);
}
