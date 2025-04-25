# Outputs for RDS module

output "endpoint" {
  description = "The connection endpoint for the RDS instance"
  value       = module.postgresql.db_instance_endpoint
}
