# Canarie GeoCENS Project
This repository contains backend delivarables for Canarie GeoCENS project.

It has two main parts, QA-QC and the Gap filling/Re-griding modules.

QA-QC part is cleaning the data based on the described valid range for the Datastream.

Gap filling/Re-griding provides a REST endpoint for getting spatiotemporal interpolated value for a specific ObservedProperty and specific time and position (latitude and longitude) based on the collected refined data from QA-QC part.

More details will be availabe at each modules, [QA-QC](wp2.1-qa-qc/README.md) and the [Gap filling/Re-griding](wp2.2-gap-filling-regriding/README.md).
