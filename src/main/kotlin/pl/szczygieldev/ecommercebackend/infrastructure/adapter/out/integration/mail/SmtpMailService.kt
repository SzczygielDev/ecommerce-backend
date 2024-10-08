package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.mail

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.out.MailService
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.mail.model.OrderConfirmationTemplateData
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class SmtpMailService(val mailSender: JavaMailSender, val ordersProjections: OrdersProjections) : MailService {
    private val mf: MustacheFactory = DefaultMustacheFactory("static/mail")
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun sendOrderConfirmationMail(orderId: OrderId) {
        val username = "Jan"
        val order = ordersProjections.findById(orderId)!!

        val data = OrderConfirmationTemplateData(
            username,
            order.orderId.id(),
            order.paymentProjection.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
            Instant.now().atZone(ZoneId.systemDefault()).format(formatter),
            order.items.map { item ->
                OrderConfirmationTemplateData.ProductItem(
                    item.title.value,
                    item.quantity,
                    item.price.amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
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
        helper.setSubject("Zamówienie ${order.orderId.id()}")
        helper.setText(payload, true)

        mailSender.send(mimeMessage)
    }
}