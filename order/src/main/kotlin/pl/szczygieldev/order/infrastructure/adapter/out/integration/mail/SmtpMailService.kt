package pl.szczygieldev.order.infrastructure.adapter.out.integration.mail

import arrow.core.raise.either
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import pl.szczygieldev.order.application.port.out.MailService
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.infrastructure.adapter.out.integration.mail.model.OrderConfirmationTemplateData
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
internal class SmtpMailService(val mailSender: JavaMailSender, val ordersProjections: OrdersProjections) : MailService {
    private val mf: MustacheFactory = DefaultMustacheFactory("static/mail")
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun sendOrderConfirmationMail(orderId: OrderId) = either{
        val username = "Jan"
        val order = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))

        val data = OrderConfirmationTemplateData(
            username,
            order.orderId.id().uppercase(),
            order.paymentProjection.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
            Instant.now().atZone(ZoneId.systemDefault()).format(formatter),
            order.items.map { item ->
                OrderConfirmationTemplateData.ProductItem(
                    item.title,
                    item.quantity,
                    item.price.setScale(2, RoundingMode.HALF_UP).toPlainString()
                )
            }
        )

        val writer: Writer = StringWriter()
        val mustache = mf.compile("template.html")
        mustache.execute(
            writer,
            data
        )
        writer.close()
        val payload = writer.toString()

        val mimeMessage = mailSender.createMimeMessage()

        val helper = MimeMessageHelper(mimeMessage)
        helper.setFrom("system@example.com")
        helper.setTo("user@example.com")
        helper.setSubject("Zamówienie ${order.orderId.id().uppercase()}")
        helper.setText(payload, true)

        mailSender.send(mimeMessage)
    }
}